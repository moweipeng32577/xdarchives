/**
 * Created by Leo on 2019/5/15 0015.
 */

//视频播放插件
    function xdvideo() {
    }
//提供默认参数
    xdvideo.prototype = {
        options: {
            isPlay: true,  //自动播放
            width: 800,//px //宽
            height: 600,//px    //长
            entryid:0,
            videoUrl:'',//播放视频的地址
            fatherDivId: 'tapdiv',
            type:'flv'  //视频的类型 默认flv
        },
        create:function(opt){
            if(opt)
                for (var key in opt) {
                    this.options[key] = opt[key];
                }
            if(this.options.fatherDivId != null && this.options.fatherDivId != undefined && this.options.fatherDivId != ""){
                var me = this;
                // var videoHtml = '<video id="videoElement" controls width="'+me.options.width+'px" height="'+ me.options.height +'px"></video>';
                var videoHtml = '<div class="video-container"> <div> <video id="videoElement" name="videoElement" class="centeredVideo" controls>Your browser is too old which doesn\'t support HTML5 video. </video> </div> </div>';
                document.getElementById(me.options.fatherDivId).style.width = me.options.width + "px";
                document.getElementById(me.options.fatherDivId).style.height = me.options.height + "px";
                document.getElementById(me.options.fatherDivId).innerHTML = videoHtml;
                if (flvjs.isSupported()) {
                    var videoElement = document.getElementById('videoElement');
                    var flvPlayer = flvjs.createPlayer({
                        cors:true,
                        type: me.options.type,
                        url: me.options.videoUrl
                    });
                    flvPlayer.attachMediaElement(videoElement);
                    flvPlayer.load();
                    flvPlayer.pause();
                    if(me.options.isPlay == true)
                        flvPlayer.play();
                }
            }
            else{
            }
        }
    };
var xvideo = new xdvideo();
    window.xdvideo = xvideo;



