//文件上传弹出框
Ext.define('Watermark.view.WatermarkMediaView', {
    extend: 'Ext.panel.Panel',
    xtype: 'WatermarkMediaView',
    layout: 'border',
    bodyBorder: false,
    isAdd:true,//是否为增加操作
    isLook:false,//是否隐藏选择按钮
    isTop:true,//是否按钮工具栏顶部居左显示
    uploadUrl:'',//电子文件上传路径
    mediaUrl:'',//电子文件显示路径
    selUploadMediaUrl:'',//查找上传的电子文件
    mediaType:{},//电子文件现在类型
    selTitle:'选择',//选择按钮文本
    eleid:'',//选择电子文件的id
    isDigital:false,//是否为数码照片
    watermarkPath:'',//数码照片拍摄时间
    initComponent: function() {
        var me = this;
        var buttons = ['->',{
            //此处实现文件选择
            xtype: 'displayfield',
            height: 37,
            id: 'picker',
            hidden:me.isLook
        }, {
            xtype: 'button',
            itemId:'mediaViewBack',
            width: 100,
            height: 37,
            text: '返回',
            style:{'background-color':'white'}
        }];

        var region = 'south';

        if(me.isTop){
            buttons = [{
                //此处实现文件选择
                xtype: 'displayfield',
                height: 37,
                id: 'picker',
                hidden:me.isLook
            }];

            region = 'north';
        }

        me.items = [{
            region: region,
            bbar: buttons,

            uploadFrame:function(){
                if(arguments.length>0){
                    this.ownerCt.mediaUrl = this.ownerCt.mediaUrl.substring(0,this.ownerCt.mediaUrl.indexOf('=')+1);
                }
                var path = arguments[0];
                // var url = path?this.ownerCt.mediaUrl+path:this.ownerCt.mediaUrl;
                // var mediaFrame = document.getElementById('mediaFrame');
                // mediaFrame.setAttribute('src',url);
                var waterMedia = document.getElementById('waterMedia');
                var newurl = encodeURI('/electronic/watermarkloadMedia?watermarkPath='+(path?path:this.ownerCt.mediaUrl));
                waterMedia.setAttribute('src',newurl);
            },

            listeners: {
                render: function (win) {
                    //console.log(win.mediaUrl);
                    //初始化文件上传组件
                    win.uploader = WebUploader.create({
                        //swf文件路径
                        swf: '/js/Uploader.swf',
                        //文件接收服务端
                        server: win.ownerCt.uploadUrl,
                        //选择文件的按钮,可选
                        //内部根据当前运行是创建,可能是input元素,也可能是flash
                        pick: {
                            id: '#picker',
                            label: win.ownerCt.selTitle
                        },
                        //是否要分片处理大文件上传(断点续传)
                        chunked: true,
                        //文件分片大小,5M
                        chunkSize: 5242880,
                        //某个分片由于网络问题出错,自动重传次数
                        chunkRetry: 3,
                        //上传并发数
                        threads: 3,
                        //单文件大小限制,5000M
                        fileSingleSizeLimit: 5242880000,
                        accept: win.ownerCt.mediaType
                    });
                    //监听文件选择时间,将选中的文件信息添加到列表中
                    win.uploader.on('filesQueued', function () {
                        Ext.Msg.wait("",{text:"正在上传..."});
                        //上传文件
                        win.uploader.upload();
                    });
                    //监听文件上传进度,更新列表上传进度条
                    win.uploader.on('uploadPropress', function (file, progress) {
                        console.log(progress);
                    });
                    //监听文件上传成功,提示用户
                    win.uploader.on('uploadSuccess', function (file, response) {
                            if(response.success){
                                win.ownerCt.watermarkPath = response.msg;
                                win.uploadFrame(win.ownerCt.watermarkPath);
                                //console.log(win.ownerCt.watermarkPath);
                            }else{
                                XD.msg(response.msg)
                            }

                        Ext.Msg.hide();
                        XD.msg('上传成功');
                    });

                    if(!win.ownerCt.isAdd){
                        win.uploadFrame();
                    }
                }
            }
        }, {
            region: 'center',
            // html: '<iframe id="mediaFrame" src="" width="100%" height="100%" style="border:0px;"></iframe>'
            html: '<div style="position: relative;height: 100%;"><img id="waterMedia" src="" style="position: absolute;top:0;left:0;right: 0;bottom: 0;margin: auto;height:300px;"/></div>'
        }];
        this.callParent();
    }

});