package com.wsy.crm.workbench.web.controller;

import com.wsy.crm.commons.constant.Constant;
import com.wsy.crm.commons.domain.ReturnObject;
import com.wsy.crm.commons.utils.DateUtils;
import com.wsy.crm.commons.utils.HSSFUtils;
import com.wsy.crm.commons.utils.UUIDUtils;
import com.wsy.crm.settings.domain.User;
import com.wsy.crm.settings.service.UserService;
import com.wsy.crm.workbench.domain.Activity;
import com.wsy.crm.workbench.domain.ActivityRemark;
import com.wsy.crm.workbench.service.ActivityRemarkService;
import com.wsy.crm.workbench.service.ActivityService;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.*;

@Controller
public class ActivityController {

    @Autowired
    private UserService userService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ActivityRemarkService activityRemarkService;
    /**
     * 通过该控制器可以将用户信息存放在request中，并跳转至workbench/activity/index.jsp页面
     * @param request
     * @return
     */
    @RequestMapping("/workbench/activity/index.do")
    public String index(HttpServletRequest request){
        //调用Service层来查询所有用户
        List<User> userList = userService.queryAllUsers();
        //将所有用户保存至request中
        request.setAttribute("userList",userList);
        //请求转发
        return "workbench/activity/index";
    }

    /**
     * 由于前台是ajax发送的异步请求，因此传给前台的响应不能是页面，而是json对象
     * 因此返回值为Object类型，并加上@ResponseBody注解
     * 参数传入Activity对象，前台填在表单中的数据会自动封装在对象中
     * 然而，在后台的sql语句创建市场活动时，需要传入九个参数，前台只能给三个参数，因此剩下的需要在此控制器方法中实现
     * @return
     */
    @RequestMapping("/workbench/activity/saveCreateActivity.do")
    @ResponseBody
    public Object saveCreateActivity(Activity activity, HttpSession session){
        //剩下三个参数没有给出，需要进行二次封装
        //id参数——采取UUID生成32位uuid作为id,并将这个方法封装在工具类中，在此调用
        activity.setId(UUIDUtils.getUUID());
        //设置创建时间参数
        activity.setCreateTime(DateUtils.formatDateTime(new Date()));
        //设置创建者，创建者是当前session中的用户，因此需要在形参处传入session对象
        //引入的应当是user表中一条记录，也就是一个用户的标识。一条记录的标识是主键，如果使用名字则无法避免重名情况
        //因此应将作为标识的用户记录的id（主键），赋予此形参
        User user = (User)session.getAttribute(Constant.SESSION_USER);
        activity.setCreateBy(user.getId());
        //调用service层方法，创建市场活动，将此记录通过Mapper层记录到数据库中,并封装成java对象用于响应ajax异步请求
        ReturnObject returnObject = new ReturnObject();
        //对于读数据，我们一般不关心查没查出来
        //对于写数据，数据写入的成功与否至关重要，如果失败则意味着出现了异常应该回滚
        //因此在这里，我们把调用service层进行数据写入的方法放在try...catch块中
        try{
            int ret = activityService.saveCreateActivity(activity);
            if(ret <= 0){
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统忙，请稍后重试...");
            }else{
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
            }
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试...");
        }
        return returnObject;
    }

    /**
     * 根据市场活动页面传来的具体查询条件，按照要求查询市场活动记录，并根据前端分页要求进行具体的分页
     * @param name 市场活动名称
     * @param owner 所有者
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param pageNo 页码数
     * @param pageSize 每页记录数
     * @return 由于前端发送的是异步请求（局部刷新页面），因此应当返回给前端经后端封装好的java对象
     */
    @RequestMapping("/workbench/activity/queryActivityByConditionForPage.do")
    @ResponseBody
    public Object queryActivityByConditionForPage(String name, String owner, String startDate,
                                                    String endDate, int pageNo, int pageSize){
        //封装参数
        Map<String,Object> map = new HashMap<>();
        map.put("name",name);
        map.put("owner",owner);
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        //开始记录数应当在controller层进行计算
        map.put("beginNo",(pageNo - 1) * pageSize);
        map.put("pageSize",pageSize);

        //调用service层方法，查询数据
        List<Activity> activityList = activityService.queryActivityByConditionForPage(map);
        int totalRows = activityService.queryCountOfActivityByCondition(map);
        //把这两个结果封装起来传给前端
        //封装可以考虑封装到一个java类对象中，这样需要创建一个java实体类，然后把这两个数据封装在里面
        //也可以直接封装在一个map里面
        Map<String,Object> retMap = new HashMap<>();
        retMap.put("activityList",activityList);
        retMap.put("totalRows",totalRows);
        return retMap;
    }

    @RequestMapping("/workbench/activity/deleteActivityIds.do")
    @ResponseBody
    public Object deleteActivityIds(String[] id){
        //创建返回的对象
        ReturnObject returnObject = new ReturnObject();
        try{
            //调用service层方法，删除市场活动
            int ret = activityService.deleteActivityByIds(id);
            if(ret <= 0){
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统忙，请稍后重试...");
            }else{
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
            }
        }catch(Exception e){
            returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试...");
        }
        return returnObject;
    }

    /**
     * 根据id来查询某一个市场活动，用于模态窗口显示要修改的市场活动原始信息
     * @param id
     * @return
     */
    @RequestMapping("/workbench/activity/queryActivityById.do")
    @ResponseBody
    public Object queryActivityById(String id){
        //调用service层方法
        Activity activity = activityService.queryActivityById(id);
        //根据查询结果返回响应信息
        return activity;
    }

    /**
     * 实现保存前端修改市场活动的功能
     * @param activity
     * @param session
     * @return
     */
    @RequestMapping("/workbench/activity/saveEditActivity.do")
    @ResponseBody
    public Object saveEditActivity(Activity activity, HttpSession session){
        //获取用户
        User user = (User)session.getAttribute(Constant.SESSION_USER);
        //封装参数
        activity.setEditTime(DateUtils.formatDateTime(new Date()));
        //用户名不唯一，因此在此处应当存储用户对应的id
        activity.setEditBy(user.getId());
        //调用service层方法
        ReturnObject returnObject = new ReturnObject();
        try{
            int ret = activityService.saveEditActivity(activity);
            if(ret <= 0){
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统忙，请稍后重试...");
            }else{
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
            }
        }catch (Exception e){
            returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试...");
        }
        return returnObject;
    }

    /**
     * 查询所有市场活动信息，用于excel导出
     * @param response
     */
    //这里的返回值是void，是由于前台发的是同步请求，不需要传回json字符串用于解析并渲染页面，也不需要进行页面跳转
    //只是通过控制器查询后台数据库的数据，并在前端弹出下载的请求
    @RequestMapping("/workbench/activity/exportAllActivitys.do")
    public void exportAllActivitys (HttpServletResponse response)throws Exception{
        //调用service层方法，查询所有市场活动
        List<Activity> activityList = activityService.queryAllActivitys();
        //使用apache-poi插件，创建excel文件，并且把activityList中数据写入到excel文件中
        //创建HSSFWorkBook对象，这个对象代表excel文件
        HSSFWorkbook wb = new HSSFWorkbook();
        //创建一个数据页，用于写入数据
        HSSFSheet sheet = wb.createSheet("市场活动");
        //通过使用sheet页面创建数据行HSSFRows对象，代表sheet中的一行
        HSSFRow row = sheet.createRow(0);//数据行从下标0开始计算，0指的是第一行
        //通过使用row创建数据列HSSFCell对象，代表sheet中的一列
        //写入表头的列名
        HSSFCell cell = row.createCell(0);//数据列从下标0开始计算，0指的是第一列

        cell.setCellValue("ID");
        cell = row.createCell(1);
        cell.setCellValue("所有者");
        cell = row.createCell(2);
        cell.setCellValue("名称");
        cell = row.createCell(3);
        cell.setCellValue("开始日期");
        cell = row.createCell(4);
        cell.setCellValue("结束日期");
        cell = row.createCell(5);
        cell.setCellValue("成本");
        cell = row.createCell(6);
        cell.setCellValue("描述");
        cell = row.createCell(7);
        cell.setCellValue("创建时间");
        cell = row.createCell(8);
        cell.setCellValue("创建者");
        cell = row.createCell(9);
        cell.setCellValue("修改时间");
        cell = row.createCell(10);
        cell.setCellValue("修改者");
        //遍历list集合，常见HSSFRows对象，填入数据，生成所有的数据行
        if(activityList!= null && activityList.size() != 0){
            Activity activity = null;
            for(int i = 0; i < activityList.size(); i++){
                activity = activityList.get(i);
                //每遍历出一个市场活动对象，生成一行
                row = sheet.createRow(i + 1);//第一行是表头
                //每一行创建11列，每一列的数据从activity对象的属性中获取
                cell = row.createCell(0);
                cell.setCellValue(activity.getId());
                cell = row.createCell(1);
                cell.setCellValue(activity.getOwner());
                cell = row.createCell(2);
                cell.setCellValue(activity.getName());
                cell = row.createCell(3);
                cell.setCellValue(activity.getStartDate());
                cell = row.createCell(4);
                cell.setCellValue(activity.getEndDate());
                cell = row.createCell(5);
                cell.setCellValue(activity.getCost());
                cell = row.createCell(6);
                cell.setCellValue(activity.getDescription());
                cell = row.createCell(7);
                cell.setCellValue(activity.getCreateTime());
                cell = row.createCell(8);
                cell.setCellValue(activity.getCreateBy());
                cell = row.createCell(9);
                cell.setCellValue(activity.getEditTime());
                cell = row.createCell(10);
                cell.setCellValue(activity.getEditBy());
            }
        }

/*        //调用工具函数生成excel文件
        OutputStream os = new FileOutputStream("E:\\xuexi\\编程\\项目\\CRM\\serverDir\\activityList.xls");
        wb.write(os);
        //关闭资源
        os.close();
        wb.close();*/

        //把生成的excel文件下载到客户端
        //1.设置响应的类型
        response.setContentType("application/octet-stream;charset=UTF-8");
        //浏览器在接收到信息时，默认情况下直接在显示窗口打开信息，即使打不开也会调用应用程序打开；只有实在打不开的情况，才会调用下载
        //根据本需求，我们应当要求浏览器在收到本次响应信息后，无论能不能打开都不直接打开，直接激活下载窗口，使客户端得以下载文件
        //这个要求通过添加响应头可以实现
        response.addHeader("Content-Disposition","attachment;filename=activityList.xls");
        //2.获取输出流
        OutputStream out = response.getOutputStream();
/*        //3.通过输入流读取磁盘中的excel文件，并通过输出流输出到浏览器中
        InputStream is = new FileInputStream("E:\\xuexi\\编程\\项目\\CRM\\serverDir\\activityList.xls");
        byte[] buff = new byte[256];
        int len = 0;
        while((len = is.read(buff))!= -1){
            out.write(buff,0,len);
        }
        //关闭资源
        is.close();*/

        //由于从内存写到磁盘，再从磁盘读到内存发送浏览器效率太低，因此可以注释掉上面代码，直接将内存中的wb写入到浏览器
        wb.write(out);
        wb.close();
        out.flush();//不是新建的，是通过响应对象获得的，在之后可能还要用到，由tomcat进行关闭即可，只需要把缓冲区的数据刷到浏览器端即可（防止数据丢失）
    }

    /**
     * 实现导入市场活动的功能
     * @param activityFile 使用文件上传解析器封装前端所上传的文件（因此需要在配置文件中配置文件上传解析器），封装到这个MultipartFile对象中）
     * @return
     */
    @RequestMapping("/workbench/activity/importActivity.do")
    @ResponseBody
    public Object importActivity(MultipartFile activityFile,HttpSession session){
        //把文件写入到磁盘目录中
        //把文件在服务器的指定目录中生成相同的文件
        ReturnObject returnObject = new ReturnObject();
        //此处的io异常需要捕获，因为如果出现异常，需要在前台页面给出提示信息
        try {
/*            String originalFilename = activityFile.getOriginalFilename();//由于文件格式在后台不知道是什么，存入磁盘时文件格式不能写死，而关于文件的名称以及格式信息存储在MultipartFile对象中，因此通过此方法获取然后存入，更具动态性
            File file = new File("E:\\xuexi\\编程\\项目\\CRM\\serverDir",originalFilename);
            activityFile.transferTo(file);//写入到指定磁盘位置*/

            //解析excel文件，获取文件中的数据，并封装在市场活动对象中
            //根据excel文件生成HSSFWorkbook对象，在里面封装了数据信息
/*            InputStream is = new FileInputStream(file);*/
            InputStream is = activityFile.getInputStream();//优化上述过程，实现直接从内存到内存的数据转换过程（读出内存中activityFile的数据到内存中创建的excel文件wb中）
            HSSFWorkbook wb = new HSSFWorkbook(is);
            //根据wb获取数据页sheet对象
            HSSFSheet sheet = wb.getSheetAt(0);
            //根据sheet获取数据行row对象
            HSSFRow row = null;
            HSSFCell cell = null;
            Activity activity = null;
            List<Activity> activityList = new ArrayList<>();
            //获取当前session对应的用户
            User user = (User)session.getAttribute(Constant.SESSION_USER);
            for(int i = 1; i <= sheet.getLastRowNum(); i++){//getLastRowNum()是获取sheet页中最后一行的下标，第一行是表头不是数据，因此不取第一行内容
                row = sheet.getRow(i);
                activity = new Activity();
                activity.setId(UUIDUtils.getUUID());//id值是根据算法设置，不应该交由用户来设置
                //由于在excel中如果填owner属性，肯定填的是名字，不能填入id，而后台存储这一字段是依靠id存储的
                //于是针对这一字段值的设置方案是，也不交由excel文件进行填写，默认的owner是导入市场活动的人
                activity.setOwner(user.getId());
                //创建时间这一字段就是上传的时间，因此也不用在excel中编写
                activity.setCreateTime(DateUtils.formatDateTime(new Date()));
                //创建者同样由当前用户决定
                activity.setCreateBy(user.getId());
                for(int j = 0; j < row.getLastCellNum(); j++){//getLastCellNum()是获取row行中最后一列的下标+1，注意区分开
                    cell = row.getCell(j);
                    //获取列中的数据，由于代码较长且可能经常使用，封装在工具类中
                    String cellValue = HSSFUtils.getCellValueForStr(cell);
                    if(j == 0){
                        activity.setName(cellValue);
                    }else if(j == 1){
                        activity.setStartDate(cellValue);
                    }else if(j == 2){
                        activity.setEndDate(cellValue);
                    }else if(j == 3){
                        activity.setCost(cellValue);
                    }else if(j == 4){
                        activity.setDescription(cellValue);
                    }
                }
                //每一行遍历后，封装到了一个activity对象，需要将其保存在list集合中
                activityList.add(activity);
            }
        //调用service层方法，保存市场活动数据
            int ret = activityService.saveCreateActivityByList(activityList);

            //根据处理结果,返回响应信息
            returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
            returnObject.setRetData(ret);

        } catch (IOException e) {
            e.printStackTrace();
            returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试...");
        }
        return returnObject;
    }


    /**
     * 查询某一市场活动详细信息保存在作用域中用于后续渲染页面，跳转至detail页面
     * @return
     */
    @RequestMapping("/workbench/activity/detailActivity.do")
    public String detailActivity(String id,HttpServletRequest request){
        //调用service层方法，查询相关数据
        Activity activity = activityService.queryActivityForDetailById(id);
        List<ActivityRemark> remarkList = activityRemarkService.queryActivityRemarkForDetailByActivityId(id);
        //保存到作用域中，跳转页面
        request.setAttribute("activity",activity);
        request.setAttribute("remarkList",remarkList);
        return "workbench/activity/detail";
    }
}
