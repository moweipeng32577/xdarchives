package com.wisdom.web.service;


//import com.sun.image.codec.jpeg.JPEGCodec;
//import com.sun.image.codec.jpeg.JPEGImageEncoder;

import com.wisdom.service.websocket.WebSocketService;
import com.wisdom.util.MetadataUtil;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MediaCompressionService {

    @Value("${compression.tool.path}")//压缩工具路径
    private String compressionToolPath;

    @Value("${system.document.rootpath}")//系统文件根目录
    private String rootpath;

    @Value("${isThumbnail}")//是否需要缩略图
    private boolean isThumbnail;

    @Value("${thumbnail.resolution}")//缩略图分辨率
    private String thumbnailResolution;

    @Value("${file.resolution}")//媒体文件压缩后分辨率
    private String fileResolution;

    @Value("${system.nginx.browse.path}")//浏览文件路径
    private String browsepath;
    @Value("${thumbnail.temp.path}")
    private String thumbnailTempPath;//生成临时文件缩略图的路径

    @Autowired
    public WebSocketService webSocketService;

//    @Value("${thumbnail.path}")//生成缩略图路径
//    private String thumbnailPath;

    static private int COMPLETE = 0;

    public boolean process(String relativePath, int mediaType, String filename) {

        String PATH = rootpath + relativePath;
        if (!checkfile(relativePath)) {
            System.out.println(PATH + " 不存在或不是文件");
            return false;
        }
        int type = checkContentType(PATH);
        boolean status = false;
        if (1 == mediaType) {
            if (type == 0) {
                System.out.println("文件压缩中...");
                status = processMinMp4(relativePath,filename);// 直接将文件转为mp4文件
            } else if (type == 1) {
                String avifilepath = processAVI(type, relativePath);
                if (avifilepath == null)
                    return false;// avi文件没有得到
                status = processMinMp4(avifilepath,filename);// 将avi转为flv
            }
        } else if (2 == mediaType) {
            status = processMinMp3(relativePath);// 直接将文件转为flv文件
        } else if (3 == mediaType) {
            status = reduceImg(relativePath, 1, 1, 0.5f);
        }

        return status;
    }

    private int checkContentType(String PATH) {
        String type = PATH.substring(PATH.lastIndexOf(".") + 1, PATH.length())
                .toLowerCase();
        // ffmpeg能解析的格式：（asx，asf，mpg，wmv，3gp，mp4，mov，avi，flv等）
        if (type.equals("avi")) {
            return 0;
        } else if (type.equals("mpg")) {
            return 0;
        } else if (type.equals("wmv")) {
            return 0;
        } else if (type.equals("3gp")) {
            return 0;
        } else if (type.equals("mov")) {
            return 0;
        } else if (type.equals("mp4")) {
            return 0;
        } else if (type.equals("asf")) {
            return 0;
        } else if (type.equals("asx")) {
            return 0;
        } else if (type.equals("flv")) {
            return 0;
        }
        // 对ffmpeg无法解析的文件格式(wmv9，rm，rmvb等),
        // 可以先用别的工具（mencoder）转换为avi(ffmpeg能解析的)格式.
        else if (type.equals("wmv9")) {
            return 1;
        } else if (type.equals("rm")) {
            return 1;
        } else if (type.equals("rmvb")) {
            return 1;
        }
        return 9;
    }

    private boolean checkfile(String path) {
        File file = new File(rootpath + path);
        String filename = file.getName();
        int mediaNum = getMediaType(filename.substring(filename.lastIndexOf('.') + 1));
        if (!file.isFile()&&mediaNum==0) {
            return false;
        } else {
            File file1 = new File(browsepath + "/browse" + path).getParentFile();
            if (!file1.exists()) {
                file1.mkdirs();
            }

//            if (isThumbnail) {
//                File file2 = new File(thumbnailPath + path.substring(0, path.lastIndexOf(".")) + ".jpg").getParentFile();
//                if (!file2.exists()) {
//                    file2.mkdirs();
//                }
//            }
        }
        return true;
    }
    public int getMediaType(String type) {
        List<String> imgs = Arrays.asList("jpg", "jpeg", "png", "tif", "gif", "bmp");
        List<String> audios = Arrays.asList("wav", "mp3", "wma", "acc");
        List<String> videos = Arrays.asList("avi", "mp4", "rmvb", "flv");
        if (imgs.contains(type.toLowerCase())) {
            return 3;
        } else if (audios.contains(type.toLowerCase())) {
            return 2;
        } else if (videos.contains(type.toLowerCase())) {
            return 1;
        }
        return 0;
    }

    // 对ffmpeg无法解析的文件格式(wmv9，rm，rmvb等), 可以先用别的工具（mencoder）转换为avi(ffmpeg能解析的)格式.
    private String processAVI(int type, String relativePath) {
        List<String> commend = new ArrayList<String>();
        commend.add(compressionToolPath + "\\mencoder");
        commend.add(rootpath + relativePath);
        commend.add("-oac");
        commend.add("lavc");
        commend.add("-lavcopts");
        commend.add("acodec=mp3:abitrate=64");
        commend.add("-ovc");
        commend.add("xvid");
        commend.add("-xvidencopts");
        commend.add("bitrate=600");
        commend.add("-of");
        commend.add("avi");
        commend.add("-o");
        commend.add(browsepath + "/browse" + relativePath.substring(0, relativePath.lastIndexOf(".")) + ".avi");
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commend);
            builder.start();
            return browsepath + "/browse" + relativePath.substring(0, relativePath.lastIndexOf(".")) + ".avi";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ffmpeg能解析的格式：（asx，asf，mpg，wmv，3gp，mp4，mov，avi，flv等）
    private boolean processMinMp4(String oldfilepath, String filename) {
        // 文件命名
        Calendar c = Calendar.getInstance();
        String savename = String.valueOf(c.getTimeInMillis()) + Math.round(Math.random() * 100000);
        List<String> commend = new ArrayList<String>();
        commend.add(compressionToolPath + "ffmpeg");
        commend.add("-i");//设定输入流或输入路径
        commend.add(rootpath + oldfilepath);
        //////////////////////
//        commend.add("-f");//输出格式
//        commend.add("mp4");
//        ////////////////////
//        commend.add("-ab");
//        commend.add("56");
//        commend.add("-ar");//采样率，直接关系到视频质量，值越大越清晰
//        commend.add("22050");
//        //–vcodec h264
//        commend.add("-vcodec");//视频编码
//        commend.add("h264");
//        ////////////////
//        commend.add("-qscale");
//        commend.add("8");
//        commend.add("-r");//帧速率
//        commend.add("30");
        //commend.add("-s");//设定画面的宽和高
        //commend.add(fileResolution);
        //commend.add(browsepath + "/browse"+oldfilepath.replaceAll(".mp4",".flv"));


        commend.add("-y");//覆盖
        Map<String, String> map;
        try{
            map = MetadataUtil.getVideoInfos(rootpath + oldfilepath,compressionToolPath);
        }catch(IOException e){
            e.printStackTrace();
            map = null;
        }
        if(map != null
                && map.get("CodecID") != null
                && (map.get("CodecID").toLowerCase()).indexOf("h264") != -1){//说明原本的编码是h264
            commend.add("-c:v");
            commend.add("copy");
            commend.add("-c:a");
            commend.add("copy");
        }else {
            commend.add("-vf");
            commend.add("scale=520:-1");//分辨率：宽固定，高按比例
            commend.add("-ab");//音频数据流
            commend.add("56");
//        commend.add("-acodec");
//        commend.add("mp3");
            commend.add("-ac");//声道数
            commend.add("2");
            commend.add("-ar");//声音采样频率
            commend.add("22050");
            commend.add("-b");//固定码率
            commend.add("800000");
            commend.add("-r");//帧率
            commend.add("24");
        }
        commend.add(browsepath + "/browse" + oldfilepath.replaceAll(oldfilepath.substring(oldfilepath.lastIndexOf(".")), ".flv"));
        try {
            Runtime runtime = Runtime.getRuntime();
            String thumbCut = compressionToolPath + "ffmpeg.exe    -ss   00:00:00   -i   "
                    + rootpath + oldfilepath
                    + "   -y   -f   image2  -t   0.001   -vf   scale=130:-1   " + browsepath + "/thumbnail" + oldfilepath.substring(0, oldfilepath.lastIndexOf(".")) + ".jpg";
            List<String> thumbCutList = new ArrayList<>();
            thumbCutList.add(compressionToolPath + "ffmpeg");
            thumbCutList.add("-ss");
            thumbCutList.add("00:00:00");
            thumbCutList.add("-i");
            thumbCutList.add("\"" + rootpath + oldfilepath + "\"");
            thumbCutList.add("-y");
            thumbCutList.add("-f");
            thumbCutList.add("image2");
            thumbCutList.add("-t");
            thumbCutList.add("0.001");
            thumbCutList.add("-vf");
            thumbCutList.add("scale=130:-1");
            thumbCutList.add("\"" + browsepath + "/thumbnail" + oldfilepath.substring(0, oldfilepath.lastIndexOf(".")) + ".jpg" + "\"");


            String tempStr = browsepath + "/thumbnail" + oldfilepath.substring(0, oldfilepath.lastIndexOf("/")) + "/temp/"+filename+ File.separator;
            File tempDir = new File(tempStr);
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }
//            String previewCut = compressionToolPath + "ffmpeg.exe -ss 00:00:00 -i " + rootpath + oldfilepath + " -r " + 19 / getDuration(oldfilepath) + " -vf scale=130:-1 "
//                    + tempStr + oldfilepath.substring(oldfilepath.lastIndexOf("/") + 1, oldfilepath.lastIndexOf(".")) + "_ROLL%02d.jpg";
            String splitStr = oldfilepath.substring(oldfilepath.lastIndexOf("/") + 1, oldfilepath.lastIndexOf(".")).split("\\\\")[0];
            File splitLib = new File(tempStr+splitStr+ File.separator);
            if (!splitLib.exists()) {
                splitLib.mkdirs();
            }

            List<String> previewCutList = new ArrayList<>();
            previewCutList.add(compressionToolPath + "ffmpeg");
            previewCutList.add("-ss");
            previewCutList.add("00:00:00");
            previewCutList.add("-y");
            previewCutList.add("-i");
            previewCutList.add("\"" + rootpath + oldfilepath + "\"");
            previewCutList.add("-r");
            previewCutList.add(19 / getDuration(oldfilepath) + "");
            previewCutList.add("-vf");
            previewCutList.add("scale=130:-1");
            previewCutList.add("\"" + tempStr + oldfilepath.substring(oldfilepath.lastIndexOf("/") + 1, oldfilepath.lastIndexOf(".")) + "_ROLL%02d.jpg" + "\"");
            Process previewCutProcess = null;
            if (isThumbnail) {
                new ProcessBuilder(thumbCutList).redirectErrorStream(true).start();
               previewCutProcess = new ProcessBuilder(previewCutList).redirectErrorStream(true).start();
                new PrintStream(previewCutProcess.getErrorStream()).start();
                new PrintStream(previewCutProcess.getInputStream()).start();
            }

            //解决jvm缓存大小限制问题
            Process videoProcess = new ProcessBuilder(commend).redirectErrorStream(true).start();
            new PrintStream(videoProcess.getErrorStream()).start();
            new PrintStream(videoProcess.getInputStream()).start();
//            videoProcess.waitFor();
            if(previewCutProcess != null) {
                while(previewCutProcess.isAlive()){//乐观加锁，没停不给出去
                    continue;
                }
            }
//            if(previewCutProcess != null)
//                previewCutProcess.waitFor();

//            ProcessBuilder builder = new ProcessBuilder(commend);
//            builder.command(commend);
//            builder.start();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private float getDuration(String oldfilepath) {
        float seconds = 0;
        List<String> commands = new ArrayList<>();
        commands.add(compressionToolPath + "ffmpeg");
        commands.add("-i");//设定输入流或输入路径
        commands.add(rootpath + oldfilepath);
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commands);
            Process p = builder.start();

            //从输入流中读取视频信息
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line = "";
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
            }
            br.close();

            String regexDuration = "Duration: (.*?), start: (.*?), bitrate: (\\d*) kb\\/s";
            Matcher m = Pattern.compile(regexDuration).matcher(stringBuilder.toString());
            if (m.find()) {
                seconds = getSeconds(m.group(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return seconds;
    }

    private float getSeconds(String time) {
        float count = 0;
        String[] timeAry = time.split(":");
        if (timeAry[0].compareTo("0") > 0) {
            count += Integer.valueOf(timeAry[0]) * 60 * 60;
        }
        if (timeAry[1].compareTo("0") > 0) {
            count += Integer.valueOf(timeAry[1]) * 60;
        }
        if (timeAry[2].compareTo("0") > 0) {
            count += Float.valueOf(timeAry[2]);
        }
        return count;
    }

    public static void doWaitPro(Process p) {
        try {
            String errorMsg = readInputStream(p.getErrorStream(), "error");
            String outputMsg = readInputStream(p.getInputStream(), "out");
            int c = p.waitFor();
            if (c != 0) {// 如果处理进程在等待
                System.out.println("处理失败：" + errorMsg);
            } else {
                System.out.println(99 + outputMsg);
            }
        } catch (IOException e) {
            // tanghui Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // tanghui Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static String readInputStream(InputStream is, String f) throws IOException {
        // 将进程的输出流封装成缓冲读者对象
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuffer lines = new StringBuffer();// 构造一个可变字符串
        long totalTime = 0;
        // 对缓冲读者对象进行每行循环
        for (String line = br.readLine(); line != null; line = br.readLine()) {
            lines.append(line);// 将每行信息字符串添加到可变字符串中
            int positionDuration = line.indexOf("Duration:");// 在当前行中找到第一个"Duration:"的位置
            int positionTime = line.indexOf("time=");
            if (positionDuration > 0) {// 如果当前行中有"Duration:"
                String dur = line.replace("Duration:", "");// 将当前行中"Duration:"替换为""
                dur = dur.trim().substring(0, 8);// 将替换后的字符串去掉首尾空格后截取前8个字符
                int h = Integer.parseInt(dur.substring(0, 2));// 封装成小时
                int m = Integer.parseInt(dur.substring(3, 5));// 封装成分钟
                int s = Integer.parseInt(dur.substring(6, 8));// 封装成秒
                totalTime = h * 3600 + m * 60 + s;// 得到总共的时间秒数
            }
            if (positionTime > 0) {// 如果所用时间字符串存在
                // 截取包含time=的当前所用时间字符串
                String time = line.substring(positionTime, line
                        .indexOf("bitrate") - 1);
                time = time.substring(time.indexOf("=") + 1, time.indexOf("."));// 截取当前所用时间字符串
                int h = Integer.parseInt(time.substring(0, 2));// 封装成小时
                int m = Integer.parseInt(time.substring(3, 5));// 封装成分钟
                int s = Integer.parseInt(time.substring(6, 8));// 封装成秒
                long hasTime = h * 3600 + m * 60 + s;// 得到总共的时间秒数
                float t = (float) hasTime / (float) totalTime;// 计算所用时间与总共需要时间的比例
                COMPLETE = (int) Math.ceil(t * 100);// 计算完成进度百分比
            }
            System.out.println("完成：" + COMPLETE + "%");
        }
        br.close();// 关闭进程的输出流
        return lines.toString();
    }

    private boolean processMinMp3(String oldfilepath) {
        // 文件命名
        Calendar c = Calendar.getInstance();
        String savename = String.valueOf(c.getTimeInMillis()) + Math.round(Math.random() * 100000);
        List<String> commend = new ArrayList<String>();
        commend.add(compressionToolPath + "ffmpeg");
        commend.add("-i");
        commend.add(rootpath + oldfilepath);
        commend.add("-b:a");
        commend.add("192k");
        commend.add("-acodec");
        commend.add("mp3");
        commend.add("-ar");
        commend.add("44100");
        commend.add("-ac");
        commend.add("2");
        commend.add(browsepath + "/browse" + oldfilepath);

        Runtime run = null;
        try {
            run = Runtime.getRuntime();
            Process p = run.exec(compressionToolPath + "ffmpeg" + " -i \"" + rootpath + oldfilepath + "\" -acodec libmp3lame  \"" + browsepath + "/browse" + oldfilepath.substring(0, oldfilepath.lastIndexOf(".")) + ".mp3\"");
            //释放进程
            p.getOutputStream().close();
            p.getInputStream().close();
            p.getErrorStream().close();
            p.waitFor();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 采用指定宽度、高度或压缩比例 的方式对图片进行压缩
     *
     * @param imgsrc     源图片地址
     * @param widthdist  压缩后图片宽度（当rate==null时，必传）
     * @param heightdist 压缩后图片高度（当rate==null时，必传）
     * @param rate       压缩比例
     */
    private boolean reduceImg(String imgsrc, int widthdist,
                              int heightdist, Float rate) {
        try {
            File srcfile = new File(rootpath + imgsrc);
            // 检查文件是否存在
            if (!srcfile.exists()) {
                return false;
            }
            // 如果rate不为空说明是按比例压缩
            if (rate != null && rate > 0) {
                // 获取文件高度和宽度
                int[] results = getImgWidth(srcfile);
                if (results == null || results[0] == 0 || results[1] == 0) {
                    return false;
                } else {
                    widthdist = (int) (results[0] * rate);
                    heightdist = (int) (results[1] * rate);
                }
            }
            // 开始读取文件并进行压缩
            Image src = ImageIO.read(srcfile);
            BufferedImage tag = new BufferedImage((int) widthdist,
                    (int) heightdist, BufferedImage.TYPE_INT_RGB);

            tag.getGraphics().drawImage(
                    src.getScaledInstance(widthdist, heightdist,
                            Image.SCALE_SMOOTH), 0, 0, null);

            /*FileOutputStream outbrowse = new FileOutputStream(browsepath + "/browse" + imgsrc);
            FileOutputStream outthumbnail = new FileOutputStream(browsepath + "/thumbnail" + imgsrc.substring(0, imgsrc.lastIndexOf(".")) + ".jpg");
            JPEGImageEncoder encoderbrowse = JPEGCodec.createJPEGEncoder(outbrowse);
            JPEGImageEncoder encoderthumbnail = JPEGCodec.createJPEGEncoder(outthumbnail);
            encoderthumbnail.encode(tag);
            encoderbrowse.encode(tag);
            outthumbnail.close();
            outbrowse.close();*/

            //ImageIO对格式为jpg的图片存储性能最高。
            String browseName=browsepath + "/browse" + imgsrc.substring(0,imgsrc.lastIndexOf("."))+".jpg";
            String thumbnailName= browsepath + "/thumbnail" + imgsrc.substring(0, imgsrc.lastIndexOf(".")) + ".jpg";
            String encoderbrowse = browseName.substring(browseName.lastIndexOf(".") + 1);
            String encoderthumbnail = thumbnailName.substring(thumbnailName.lastIndexOf(".") + 1);
            ImageIO.write(tag, /*"GIF"*/ encoderbrowse /* format desired */ , new File(browseName) /* target */ );
            ImageIO.write(tag, /*"GIF"*/ encoderthumbnail /* format desired */ , new File(thumbnailName) /* target */ );
            //上传图片长宽为800*600
            Thumbnails.of(browseName).forceSize(800, 600).toFile(browseName);
            //缩略图图片长宽为200*150
            Thumbnails.of(thumbnailName).forceSize(200, 150).toFile(thumbnailName);
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * 获取图片宽度
     *
     * @param file 图片文件
     * @return 宽度
     */
    public static int[] getImgWidth(File file) {
        InputStream is = null;
        BufferedImage src = null;
        int result[] = {0, 0};
        try {
            is = new FileInputStream(file);
            src = ImageIO.read(is);
            result[0] = src.getWidth(null); // 得到源图宽
            result[1] = src.getHeight(null); // 得到源图高
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    class PrintStream extends Thread {
        InputStream __is = null;
        public PrintStream(InputStream is) {
            __is = is;
        }

        public void run() {
            try {
                while (this != null) {
                    int _ch = __is.read();
                    if (_ch != -1)
                        System.out.print((char) _ch);
                    else
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 生成临时文件缩略图 -- 采集
     * @param userId      用户id
     * @param imgsrc     源图片地址
     */
    public boolean getTempThumbnailPic(String userId, String imgsrc) {
        File testFile = new File(thumbnailTempPath + "/pic" + imgsrc.replace(rootpath.replace("/","\\"),"") + ".jpg");
        if(!testFile.getParentFile().exists())
            testFile.getParentFile().mkdirs();//查看缩略图的文件夹是否存在
        System.runFinalization();//将临时变量清空
        int widthdist = 1;
        int heightdist = 1;
        Float rate = 0.5f;
        try {
            File srcfile = new File(imgsrc);
            // 检查文件是否存在
            if (!srcfile.exists()) {
                return false;
            }
            // 如果rate不为空说明是按比例压缩
            if (rate != null && rate > 0) {
                // 获取文件高度和宽度
                int[] results = getImgWidth(srcfile);
                if (results == null || results[0] == 0 || results[1] == 0) {
                    return false;
                } else {
                    widthdist = (int) (results[0] * rate);
                    heightdist = (int) (results[1] * rate);
                }
            }
            // 开始读取文件并进行压缩
            Image src = ImageIO.read(srcfile);
            BufferedImage tag = new BufferedImage((int) widthdist,
                    (int) heightdist, BufferedImage.TYPE_INT_RGB);

            tag.getGraphics().drawImage(
                    src.getScaledInstance(widthdist, heightdist,
                            Image.SCALE_SMOOTH), 0, 0, null);

            String browseName=imgsrc;
            String thumbnailName= thumbnailTempPath + "/pic" + imgsrc.replace(rootpath.replace("/","\\"),"") + ".jpg";//由于有后缀不同，可能导致缩略图一致，故不去除源文件后缀
            String encoderbrowse = browseName.substring(browseName.lastIndexOf(".") + 1);
            String encoderthumbnail = thumbnailName.substring(thumbnailName.lastIndexOf(".") + 1);
            ImageIO.write(tag, /*"GIF"*/ encoderbrowse /* format desired */ , new File(browseName) /* target */ );
            ImageIO.write(tag, /*"GIF"*/ encoderthumbnail /* format desired */ , new File(thumbnailName) /* target */ );
            //上传图片长宽为800*600
//            Thumbnails.of(browseName).forceSize(800, 600).toFile(browseName);
            //缩略图图片长宽为200*150
            Thumbnails.of(thumbnailName).forceSize(200, 150).toFile(thumbnailName);
            String fileSrc = "/pic" + imgsrc.replace(rootpath.replace("/","\\"),"") + ".jpg";
            fileSrc = fileSrc.replace("\\","/");    //防止url无法识别
//            webSocketService.reflashCaptureDataView(userId,fileSrc);//通过Socket通知前端更缩略图新列表
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * 视频临时gif
     * @param videosrc  视频源路径
     * @return
     */
    public boolean getTempThumbnailVd(String userId, String videosrc) throws IOException, InterruptedException, ExecutionException {
        File testFile = new File(thumbnailTempPath + "/video" + videosrc.replace(rootpath.replace("/","\\"),"") + ".gif");
        if(!testFile.getParentFile().exists())
            testFile.getParentFile().mkdirs();
        System.runFinalization();
        List<String> commend = new ArrayList<String>();
        commend.add(compressionToolPath + "ffmpeg");
        commend.add("-i");
        commend.add(videosrc);  //输入文件
        commend.add("-vframes");    //将视频的前
        commend.add("200");      //将视频的前60帧转换成gif (大概要3秒一张)
        commend.add("-y");  //替换文件
        commend.add("-f");  //转换容器
        commend.add("gif");
        commend.add("-s");  //分辨率
        commend.add("200:150");
        commend.add(thumbnailTempPath + "/video" + videosrc.replace(rootpath.replace("/","\\"),"") + ".gif");
        Process videoProcess = new ProcessBuilder(commend).redirectErrorStream(true).start();
        FutureTask<Integer> taskError = new FutureTask<Integer>(new PrintStreamCall(videoProcess.getErrorStream()));
        FutureTask<Integer> taskInput = new FutureTask<Integer>(new PrintStreamCall(videoProcess.getInputStream()));
        //使用线程打印
        new Thread(taskError).start();
        new Thread(taskInput).start();
        if(taskError.get() == -1 || taskInput.get() == -1)//当线程执行失败，返回-1 会阻塞在这里制造单线程的效果
            return false;
//        SecurityUser userDetails = loginService.getSecurityUser();
        String fileSrc = "/video" + videosrc.replace(rootpath.replace("/","\\"),"") + ".gif";
        fileSrc = fileSrc.replace("\\","/");    //防止url无法识别\\
//        webSocketService.reflashCaptureDataView(userId,fileSrc);//通过Socket通知前端更缩略图新列表
        return true;
    }

    class PrintStreamCall implements Callable<Integer> {
        InputStream __is = null;

        public PrintStreamCall(InputStream is) {
            __is = is;
        }

        public Integer call() {
            try {
                while (this != null) {
                    int _ch = __is.read();
                    if (_ch != -1)
                        System.out.print((char) _ch);
                    else
                        break;
                }
                __is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    public static void main(String[] args) {
        new MediaCompressionService().process("C:\\Users\\Administrator\\Desktop\\追光者.mp3", 2,"追光者.mp3");
    }
}

