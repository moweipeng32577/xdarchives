package com.wisdom.util;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import org.apache.commons.lang3.StringUtils;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 读取照片，音视频元数据
 * Created by wjh
 */
public class MetadataUtil {

    private static MP3File mp3File;
    private static final int START=6;

    /**
     * 获取音频元数据
     */
    public static MP3AudioHeader getHead(String mp3Path){
        try {
            System.out.println("----------------Loading...Head-----------------");
            mp3File = new MP3File(mp3Path);//封装好的类
            MP3AudioHeader header = mp3File.getMP3AudioHeader();
            /*System.out.println("时长: " + header.getTrackLength()); //获得时长
            System.out.println("比特率: " + header.getBitRate()); //获得比特率
            System.out.println("音轨长度: " + header.getTrackLength()); //音轨长度
            System.out.println("格式: " + header.getFormat()); //格式，例 MPEG-1
            System.out.println("声道: " + header.getChannels()); //声道
            System.out.println("采样率: " + header.getSampleRate()); //采样率
            System.out.println("MPEG: " + header.getMpegLayer()); //MPEG
            System.out.println("MP3起始字节: " + header.getMp3StartByte()); //MP3起始字节
            System.out.println("精确的音轨长度: " + header.getPreciseTrackLength()); //精确的音轨长度*/
            return header;
        } catch (Exception e) {
            System.out.println("没有获取到任何信息");
        }
        return null;
    }

    private static void getContent() {
        try {
            System.out.println("----------------Loading...Content-----------------");
            AbstractID3v2Tag id3v2tag=  mp3File.getID3v2Tag();
            String songName=new String(id3v2tag.frameMap.get("TIT2").toString().getBytes("ISO-8859-1"),"GB2312");
            String singer=new String(id3v2tag.frameMap.get("TPE1").toString().getBytes("ISO-8859-1"),"GB2312");
            String author=new String(id3v2tag.frameMap.get("TALB").toString().getBytes("ISO-8859-1"),"GB2312");
            System.out.println("歌名："+songName.substring(START, songName.length()-3));
            System.out.println("歌手:"+singer.substring(START,singer.length()-3));
            System.out.println("专辑名："+author.substring(START,author.length()-3));
        } catch (Exception e) {
            System.out.println("没有获取到任何信息");
        }
        System.out.println("All Info："+mp3File.displayStructureAsPlainText());
    }

    /**
     * 获取图片元数据
     * @param imgFile
     * @return
     */
    public static Map<String, String> getImgMetadata(File imgFile){
        InputStream is = null;
        Map<String, String> map = new HashMap<>();
        try {
            is = new FileInputStream(imgFile);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }try {
            //核心对象操作对象
            Metadata metadata = ImageMetadataReader.readMetadata(is);//获取所有不同类型的Directory,如ExifSubIFDDirectory, ExifInteropDirectory, ExifThumbnailDirectory等,这些类均为ExifDirectoryBase extends Directory子类//分别遍历每一个Directory,根据Directory的Tags就可以读取到相应的信息
            Iterable<Directory> iterable = metadata.getDirectories();
            for (Iterator<Directory> iter = iterable.iterator(); iter.hasNext();) {
                Directory dr = iter.next();
                Collection<Tag> tags = dr.getTags();
                for (Tag tag : tags) {
                    map.put(tag.getTagName(),tag.getDescription());
                    System.out.println(tag.getTagName() + ":【" + tag.getDescription()+"】");
                }
            }
        } catch (ImageProcessingException e) {
            e.printStackTrace();} catch (IOException e) {e.printStackTrace();
        }
        return map;
    }

    /**
     * 获取视频元数据
     * @param videoPath 视频路径
     * @param ffmpegPath ffmpeg工具路径
     * @return
     * @throws Exception
     */
    public static Map<String, String> getVideoInfos(String videoPath, String ffmpegPath) throws IOException {
        Map<String, String> map = new HashMap<>();
        List<String> commands = new ArrayList<String>();
        commands.add(ffmpegPath+ "ffmpeg");
        commands.add("-i");
        commands.add(videoPath);
        //模拟cmd指令发送
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(commands);
        final Process p = builder.start();

        //从输入流中读取视频信息
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        StringBuffer sb = new StringBuffer();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
            System.out.println(line);
        }
        br.close();

        //从视频信息中解析
        String regexDuration = "Duration: (.*?), start: (.*?), bitrate: (\\d*) kb\\/s";
        String regexVideo = "Video: (.*?), (.*?), (.*?), (\\d*) kb\\/s, (.*?), (.*?)[fps,\\s]";//"(.*?), (.*?), (.*?), (\\d*) kb\\/s, (.*?)[,\\s]";
        //String regexAudio ="Audio: (\\w*), (\\d*) Hz";
        String regexAudio ="Audio: (.*?), (.*?), (.*?) ,(.*?)[,\\s]";

        Pattern pattern = Pattern.compile(regexDuration);
        Pattern pattern2 = Pattern.compile(regexVideo);
        Pattern pattern3 = Pattern.compile(regexAudio);
        Matcher m = pattern.matcher(sb.toString());
        Matcher m2 = pattern2.matcher(sb.toString());
        Matcher m3 = pattern3.matcher(sb.toString());
        if (m.find()) {
            int time = getTimelen(m.group(1));
            map.put("Duration",time+"");//视频时长
            map.put("start",m.group(2));//开始播放时间
            map.put("bitrate",m.group(3)+"kb/s");//整个文件的比特率,平均混合码率、
            map.put("fileSize", new File(videoPath).length()/1024 + "kb");
            System.out.println(videoPath+",视频时长："+time+", 开始时间："+m.group(2)+",比特率："+m.group(3)+"kb/s");
        }
        if (m2.find()) {//视频流
            int codecIndex = m2.group(1).indexOf(" (");
            if(codecIndex > 0)
                map.put("CodecID",m2.group(1).replace(m2.group(1).substring(codecIndex),""));//视频编码格式
            else
                map.put("CodecID",m2.group(1));
            map.put("",m2.group(2));//视频格式,每一帧的数据
            //不同格式的视频的解析信息不一样 有时会有 1366x768 [SAR 1:1 DAR 683:384] 类型的信息
            int replaceStartIndex = m2.group(3).lastIndexOf(" [");
            if(replaceStartIndex > 0) {
                map.put("Frame size", m2.group(3).replace(m2.group(3).substring(replaceStartIndex), ""));//分辨率
            }
            else
                map.put("Frame size", m2.group(3));//分辨率
            //防止出现  yuv420p(tv, bt709), 1366x768 [SAR 1:1 DAR 683:384]  这样的前缀数据
            int replaceEndIndex = m2.group(3).lastIndexOf("), ");
            if(replaceEndIndex > 0)
                map.put("Frame size", map.get("Frame size").replace(m2.group(3).substring(0, replaceEndIndex + 3),""));//分辨率
            map.put("code rate",m2.group(4));//视频流的比特率
            map.put("Frame rate", StringUtils.isEmpty(m2.group(6)) ? m2.group(5) : m2.group(6));//帧率
        }
        if (m3.find()) {//音频流
            map.put("aCodecID",m3.group(1));//音频编码格式
            map.put("audio quality",m3.group(2));//采样率/1000 kHz
            map.put("vocal tract",m3.group(3));//声道
            map.put("acode rate",m3.group(4));//音频流比特率
        }
        return map;
    }

    //格式:"00:00:10.68" 转化为秒数
    public static int getTimelen(String timelen) {
        int min = 0;
        String strs[] = timelen.split(":");
        if (strs[0].compareTo("0") > 0) {
            min += Integer.valueOf(strs[0]) * 60 * 60;//秒
        }
        if (strs[1].compareTo("0") > 0) {
            min += Integer.valueOf(strs[1]) * 60;
        }
        if (strs[2].compareTo("0") > 0) {
            min += Math.round(Float.valueOf(strs[2]));
        }
        return min;
    }

    public static void main(String[] args){
        File file = new File("C:\\Users\\Administrator\\Desktop\\IMG_20180418_090554_1.jpg");
        getImgMetadata(file);
    }
}
