package com.wisdom.util;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Created by Leo on 2020/8/5 0005.
 */
public class swingTest  implements ActionListener {
    JFrame frame = new JFrame("项目更新文件打包");// 框架布局
    JTabbedPane tabPane = new JTabbedPane();// 选项卡布局
    Container con = new Container();//
    JLabel label1 = new JLabel("项目目录");
    JLabel label2 = new JLabel("打包目录");
    JLabel label5 = new JLabel("开始时间");
    JTextField text1 = new JTextField();// TextField 目录的路径
    JTextField text2 = new JTextField();// 文件的路径
    JTextField text5 = new JTextField();// 日期选择
    JButton button1 = new JButton("选择");// 选择
    JButton button2 = new JButton("选择");// 选择
    JButton button5 = new JButton("选择");// 选择
    JFileChooser jfc = new JFileChooser();// 文件选择器
    JButton button3 = new JButton("确定");//

    SpinnerDateModel model = new SpinnerDateModel();
    //获得JSPinner对象
    JSpinner year = new JSpinner(model);


    swingTest(){
        jfc.setCurrentDirectory(new File("d://"));// 文件选择器的初始目录定为d盘

        double lx = Toolkit.getDefaultToolkit().getScreenSize().getWidth();

        double ly = Toolkit.getDefaultToolkit().getScreenSize().getHeight();

        year.setValue(new Date());
        //设置时间格式
        JSpinner.DateEditor editor = new JSpinner.DateEditor(year,"yyyy-MM-dd HH:mm:ss");
        year.setEditor(editor);

        frame.setLocation(new Point((int) (lx / 2) - 150, (int) (ly / 2) - 150));// 设定窗口出现位置
        frame.setSize(500, 200);// 设定窗口大小
        frame.setContentPane(tabPane);// 设置布局
        label1.setBounds(10, 10, 70, 20);
        text1.setBounds(75, 10, 320, 20);
        button1.setBounds(410, 10, 50, 20);
        label2.setBounds(10, 35, 70, 20);
        text2.setBounds(75, 35, 320, 20);
        button2.setBounds(410, 35, 50, 20);
        label5.setBounds(10, 60, 70, 20);
        year.setBounds(75, 60, 219, 22);
        button3.setBounds(10, 85, 60, 20);
        button1.addActionListener(this); // 添加事件处理
        button2.addActionListener(this); // 添加事件处理
        button3.addActionListener(this); // 添加事件处理

        con.add(label1);
        con.add(text1);
        con.add(button1);
        con.add(label2);
        con.add(text2);
        con.add(button2);
        con.add(label5);
        con.add(year);
        con.add(button3);
        frame.setVisible(true);// 窗口可见
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 使能关闭窗口，结束程序
        tabPane.add("测试面板", con);// 添加布局1

    }

    String src="";
    String tar="";

    public void stateChanged(ChangeEvent e) {

    }

    /**
     * 时间监听的方法
     */
    public void actionPerformed(ActionEvent e) {


        // TODO Auto-generated method stub
        if (e.getSource().equals(button1)) {// 判断触发方法的按钮是哪个
            jfc.setFileSelectionMode(1);// 设定只能选择到文件夹
            int state = jfc.showOpenDialog(null);// 此句是打开文件选择器界面的触发语句
            if (state == 1) {
                return;
            } else {
                File f = jfc.getSelectedFile();// f为选择到的目录
                text1.setText(f.getAbsolutePath());
                src=f.getAbsolutePath();
            }
        }
        // 绑定到选择文件，先择文件事件
        if (e.getSource().equals(button2)) {
            //jfc.setFileSelectionMode(0);// 设定只能选择到文件
            jfc.setFileSelectionMode(1);// 设定只能选择到文件
            int state = jfc.showOpenDialog(null);// 此句是打开文件选择器界面的触发语句
            if (state == 1) {
                return;// 撤销则返回
            } else {
                File f = jfc.getSelectedFile();// f为选择到的文件
                text2.setText(f.getAbsolutePath());
                tar=f.getAbsolutePath();
            }
        }

        if (e.getSource().equals(button3)) {
            // 弹出对话框可以改变里面的参数，时间很短
            Date startime=(Date)year.getValue();
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            String startTime=format.format(startime);
            getUpdateFiles(src,tar,startTime);
            System.out.println("------打包完毕------");
            //排序
            String logPath=tar+"\\ROOT"+startTime+"\\log.txt";

            //获取排序List
            java.util.List<String> txtList= readAndOrder(logPath);

            //清空txt
            clearInfoForFile(logPath);
            //按顺序写更新内容
            for(String mark:txtList){
                appendInfoToFile(logPath,  mark);
            }

            JOptionPane.showMessageDialog(null, "打包完毕！", "提示", 2);
        }
    }

    public void getUpdateFiles(String path,String targetPath,String startTime){//获取更新包
        String logPath=targetPath+"\\ROOT"+startTime;

        //String path="D:\\project\\韶关分支\\src";
        //String path="D:\\project\\iarchivesx\\src";

        String classPath=path.substring(0,path.lastIndexOf("src"))+"target\\classes";
        //String targetPath="D:\\project\\韶关分支\\ROOT\\WEB-INF\\classes";
        //String targetPath="D:\\project\\iarchivesx\\ROOT\\WEB-INF\\classes";
        targetPath=targetPath+"\\ROOT"+startTime+"\\WEB-INF\\classes";
        //String startTime="20190507080000";
        //获取src文件夹下边的所有文件，判断文件日期，获取到更新期的文件，建立相应的文件夹，
        // 如果是java文件，去target文件夹获取相应的.class文件替换.java文件
        traverseFolder(path,startTime,targetPath,classPath,logPath);
    }

    public void traverseFolder(String path,String startTime,String targetPath,String classPath,String logPath) {

        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (null == files || files.length == 0) {
                System.out.println("文件夹是空的!");
                return;
            } else {
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        System.out.println("文件夹:" + file2.getAbsolutePath());
                        traverseFolder(file2.getAbsolutePath(),startTime,targetPath,classPath,logPath);
                    } else {
                        System.out.println("文件:" + file2.getAbsolutePath());
                        //文件修改时间
                        String fileTime=getModifiedTime(file2.getAbsolutePath());
                        if(fileTime.compareTo(startTime)>0){//大于指定时间就复制文件
                            System.out.println("修改时间:"+fileTime);
                            copyFile(file2.getAbsolutePath(),targetPath,classPath,startTime,logPath);
                            try{
                                log(logPath,file2.getAbsolutePath(),"",fileTime);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } else {
            System.out.println("文件不存在!");
        }
    }

    public static String getModifiedTime(String filePath){
        File f=new File(filePath);
        long time = f.lastModified();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        cal.setTimeInMillis(time);
        //System.out.println("修改时间:" + formatter.format(cal.getTime()));
        //System.out.println("修改时间:"+time);
        return formatter.format(cal.getTime());
    }

    public void copyFile(String sourcePath,String targetPath,String classPath,String startTime,String logPath){
        if(sourcePath.contains("\\src\\main\\resources\\")){//静态资源
            //D:\project\韶关分支\src\main\resources\templates\setting\ztsize.html
            targetPath=targetPath+"\\"+sourcePath.substring(sourcePath.indexOf("resources")+10);
        }else if(sourcePath.contains("\\src\\main\\java\\")){//java文件
            String sourceTime=getModifiedTime(sourcePath);
            // D:\project\韶关分支\src\main\java\com\wisdom\web\service\DataopenService.java
            // D:\project\韶关分支\target\classes\com\wisdom\web\service\DataopenService.java
            //String classPath="D:\\project\\韶关分支\\target\\classes";
            sourcePath=classPath+"\\"+sourcePath.substring(sourcePath.indexOf("java")+5,sourcePath.lastIndexOf("."))+".class";
            targetPath=targetPath+"\\"+sourcePath.substring(sourcePath.indexOf("classes")+8);
            //文件修改时间
            String classTime=getModifiedTime(sourcePath);
            if(classTime.compareTo(sourceTime)<0) {//.class文件要比java文件迟生成
                String msg="编译文件未更新";
                try{
                    log(logPath,sourcePath,msg,classTime);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        File source=new File(sourcePath);

        String filePath = targetPath.substring(0,targetPath.lastIndexOf("\\"));
        File fp = new File(filePath);
        // 创建目录
        if (!fp.exists()) {
            fp.mkdirs();// 目录不存在的情况下，创建目录。
        }

        File dest=new File(targetPath);

        try{
            copyFileUsingJava7Files(source, dest,logPath,sourcePath,startTime);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //java7复制文件
    private static void copyFileUsingJava7Files(File source, File dest,String logPath,String sourcePath,String startTime) {
        try{
            Files.copy(source.toPath(), dest.toPath());
            //log(logPath,sourcePath,"",startTime);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //记录日志
    public static void log(String logPath,String sourcePath,String msg,String startTime) throws IOException {
        String path = logPath+"\\log.txt";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String fileTime="";
        try{
            Date date = sdf.parse(startTime);
            fileTime=formatter.format(date);
        }catch(Exception e){
            e.printStackTrace();
        }
        File file = new File(path);
        if(!file.exists()){
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        FileWriter fw = new FileWriter(file, true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(fileTime+"     "+sourcePath+"    "+msg);
        bw.write(System.getProperties().getProperty("line.separator"));//换行符
        bw.flush();
        bw.close();
        fw.close();
    }

    public static java.util.List<String> readAndOrder(String logPath){
        List<String> txtList=new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(logPath)),
                    "UTF-8"));
            String lineTxt = null;
            while ((lineTxt = br.readLine()) != null) {
                txtList.add(lineTxt);
            }
            br.close();
            if(txtList.size()>0){
                Collections.sort(txtList, new Comparator<String>(){
                    @Override
                    public int compare(String o1, String o2) {
                        if (o1.compareTo(o2) > 0){
                            return 1;
                        }else if (o1.compareTo(o2) > 0){
                            return 0;
                        }else{
                            return -1;
                        }
                    }
                });
            }

        } catch (Exception e) {
            System.err.println("read errors :" + e);
        }
        return txtList;
    }

    public static void clearInfoForFile(String fileName) {
        File file =new File(fileName);
        try {
            if(!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter =new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void appendInfoToFile(String fileName, String info) {
        File file =new File(fileName);
        try {
            if(!file.exists()){
                file.createNewFile();
            }
            FileWriter fileWriter =new FileWriter(file, true);
            info =info +System.getProperty("line.separator");
            fileWriter.write(info);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new swingTest();
    }

}
