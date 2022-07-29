package com.wsy.crm.workbench.service.impl;

import com.wsy.crm.commons.constant.Constant;
import com.wsy.crm.commons.utils.DateUtils;
import com.wsy.crm.commons.utils.UUIDUtils;
import com.wsy.crm.settings.domain.User;
import com.wsy.crm.workbench.domain.*;
import com.wsy.crm.workbench.mapper.*;
import com.wsy.crm.workbench.service.ClueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("clueService")
public class ClueServiceImpl implements ClueService {

    @Autowired
    private ClueMapper clueMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private ContactsMapper contactsMapper;

    @Autowired
    private ClueRemarkMapper clueRemarkMapper;

    @Autowired
    private CustomerRemarkMapper customerRemarkMapper;

    @Autowired
    private ContactsRemarkMapper contactsRemarkMapper;

    @Autowired
    private ClueActivityRelationMapper clueActivityRelationMapper;

    @Autowired
    private ContactsActivityRelationMapper contactsActivityRelationMapper;

    @Autowired
    private TranMapper tranMapper;

    @Autowired
    private TranRemarkMapper tranRemarkMapper;
    @Override
    public int saveCreateClue(Clue clue) {
        return clueMapper.insertClue(clue);
    }

    @Override
    public Clue queryClueForDetailById(String id) {
        return clueMapper.selectClueForDetailById(id);
    }

    @Override
    public void saveConvertClue(Map<String, Object> map) {
        //获取参数
        String clueId = (String) map.get("clueId");
        User user = (User) map.get(Constant.SESSION_USER);
        //根据Id查询线索信息
        Clue clue = clueMapper.selectClueById(clueId);
        //把线索中与公司相关的信息保存到客户表，并调用mapper保存
        Customer c = new Customer();
        c.setAddress(clue.getAddress());
        c.setOwner(user.getId());
        c.setName(clue.getCompany());
        c.setWebsite(clue.getWebsite());
        c.setContactSummary(clue.getContactSummary());
        c.setNextContactTime(clue.getNextContactTime());
        c.setDescription(clue.getDescription());
        c.setCreateBy(user.getId());
        c.setCreateTime(DateUtils.formatDateTime(new Date()));
        c.setId(UUIDUtils.getUUID());
        c.setPhone(clue.getPhone());
        customerMapper.insertCustomer(c);
        //把线索中有关个人的信息转换到联系人表（Contacts）中，并调用mapper保存
        Contacts co = new Contacts();
        co.setAddress(clue.getAddress());
        co.setAppellation(clue.getAppellation());
        co.setContactSummary(clue.getContactSummary());
        co.setCreateBy(user.getId());
        co.setCreateTime(DateUtils.formatDateTime(new Date()));
        co.setCustomerId(c.getId());//由此项赋值可知，联系人表的customerid字段依赖客户表的id字段，因此在此业务中应当先将线索转换为客户表（即先处理被依赖的表）
        co.setDescription(clue.getDescription());
        co.setEmail(clue.getEmail());
        co.setFullname(clue.getFullname());
        co.setId(UUIDUtils.getUUID());
        co.setJob(clue.getJob());
        co.setMphone(clue.getMphone());
        co.setNextContactTime(clue.getNextContactTime());
        co.setOwner(user.getId());
        co.setSource(clue.getSource());
        contactsMapper.insertContacts(co);
        //根据线索id(clueId)查询该线索下的所有备注，用于后续转换备注工作
        List<ClueRemark> crList = clueRemarkMapper.selectClueRemarkByClueId(clueId);
        //看看有没有备注，如果有备注，则需要转换到客户备注表和联系人备注表
        if(crList != null && crList.size() > 0){
            //转换为客户备注表
            //转换为联系人备注表
            CustomerRemark cu = null;
            ContactsRemark cor = null;
            List<CustomerRemark> curList = new ArrayList<>();
            List<ContactsRemark> corList = new ArrayList<>();
            for(ClueRemark cr : crList){
                cu = new CustomerRemark();
                cu.setCreateBy(cr.getCreateBy());
                cu.setCreateTime(cr.getCreateTime());
                cu.setCustomerId(c.getId());
                cu.setId(UUIDUtils.getUUID());
                cu.setEditBy(cr.getEditBy());
                cu.setEditTime(cr.getEditTime());
                cu.setEditFlag(cr.getEditFlag());
                cu.setNoteContent(cr.getNoteContent());
                curList.add(cu);

                cor = new ContactsRemark();
                cor.setCreateBy(cr.getCreateBy());
                cor.setCreateTime(cr.getCreateTime());
                cor.setContactsId(co.getId());
                cor.setId(UUIDUtils.getUUID());
                cor.setEditBy(cr.getEditBy());
                cor.setEditTime(cr.getEditTime());
                cor.setEditFlag(cr.getEditFlag());
                cor.setNoteContent(cr.getNoteContent());
                corList.add(cor);
            }
            //调用service层方法，将备注转换到客户备注表和联系人备注表
            customerRemarkMapper.insertCustomerRemarkByList(curList);
            contactsRemarkMapper.insertContactsRemarkByList(corList);
        }
        //根据clueid查询该线索与市场活动的关联关系
        List<ClueActivityRelation> carList = clueActivityRelationMapper.selectClueActivityRelationByClueId(clueId);
        //遍历线索市场活动关联关系，用于转换为联系人市场活动关联关系
        //先看看线索市场活动关联关系是否有数据
        if(carList != null && carList.size() > 0){
            ContactsActivityRelation coar = null;
            List<ContactsActivityRelation> coarList = new ArrayList<>();
            for(ClueActivityRelation car : carList){
                coar = new ContactsActivityRelation();
                coar.setContactsId(co.getId());
                coar.setId(UUIDUtils.getUUID());
                coar.setActivityId(car.getActivityId());
                coarList.add(coar);
            }
            //调用service层方法，将关联关系转换
            contactsActivityRelationMapper.insertContactsActivityRelationByList(coarList);
        }

        //如果创建了交易，则向交易表中添加一条记录
        //是否创建了交易，主要看前台传来的isCreateTran 参数是否为true，true表示需要创建交易
        String isCreateTran = (String) map.get("isCreateTran");
        if("true".equals(isCreateTran)){
            //将表单参数封装为交易的实体对象，表单的参数在map中
            Tran tran = new Tran();
            tran.setActivityId((String) map.get("activityId"));
            tran.setContactsId(co.getId());
            tran.setCreateBy(user.getId());
            tran.setCreateTime(DateUtils.formatDateTime(new Date()));
            tran.setCustomerId(c.getId());
            tran.setExpectedDate((String) map.get("expectedDate"));
            tran.setId(UUIDUtils.getUUID());
            tran.setMoney((String) map.get("money"));
            tran.setName((String) map.get("name"));
            tran.setOwner(user.getId());
            tran.setStage((String) map.get("stage"));
            //调用mapper接口，保存一条交易记录
            tranMapper.insertTran(tran);

            //若需要创建此交易，需要将该线索下的备注转换到交易备注表中
            //遍历线索备注的list
            //若线索备注存在数据，将其转换为交易备注一份
            TranRemark tr = null;
            List<TranRemark> trList = new ArrayList<>();
            if(crList != null && crList.size() > 0){
                for(ClueRemark cr : crList){
                    tr = new TranRemark();
                    tr.setCreateBy(cr.getCreateBy());
                    tr.setCreateTime(cr.getCreateTime());
                    tr.setEditBy(cr.getEditBy());
                    tr.setEditTime(cr.getEditTime());
                    tr.setEditFlag(cr.getEditFlag());
                    tr.setId(UUIDUtils.getUUID());
                    tr.setNoteContent(cr.getNoteContent());
                    tr.setTranId(tran.getId());
                }
                //调用mapper接口，将交易备注保存到表中
                tranRemarkMapper.insertTranRemarkByList(trList);
            }
        }
        //删除工作：删除线索备注，删除线索市场活动关联关系，删除线索
        //根据依赖与被依赖关系，需要先删除从表的记录，再删除主表的记录，因此需要先删除线索备注表或者线索市场活动关联关系表中的记录，再删除线索表中的记录
        //删除线索备注表中与该线索相关的线索备注
        clueRemarkMapper.deleteClueRemarkByClueId(clueId);
        //删除该线索下线索市场活动关联关系记录
        clueActivityRelationMapper.deleteClueActivityRelationByClueId(clueId);
        //删除线索表中该线索记录
        clueMapper.deleteClueById(clueId);
    }
}
