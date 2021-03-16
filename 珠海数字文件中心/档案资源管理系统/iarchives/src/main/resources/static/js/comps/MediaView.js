//文件上传弹出框
Ext.define('Comps.view.MediaView', {
    extend: 'Ext.panel.Panel',
    xtype: 'mediaView',
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
    digitalDate:'',//数码照片拍摄时间
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
                var eleid = arguments[0];
                var url = eleid?this.ownerCt.mediaUrl+eleid+'&isAdd=1':this.ownerCt.mediaUrl;
                var mediaFrame = document.getElementById('mediaFrame');
                mediaFrame.setAttribute('src',url);
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
                        //上传文件
                        win.uploader.upload();

                    });
                    //监听文件上传进度,更新列表上传进度条
                    win.uploader.on('uploadProgress', function (file, progress) {
                        Ext.Msg.hide();
                        Ext.Msg.wait("",{text:"正在上传..."});
                    });
                    //监听文件上传成功,提示用户
                    win.uploader.on('uploadSuccess', function (file, response) {
                        if(me.isDigital){
                            if(response.success){
                                win.ownerCt.digitalDate = response.msg;
                                Ext.Ajax.request({
                                    method: 'POST',
                                    url: win.ownerCt.selUploadMediaUrl+ '/' + file.name + '/',
                                    success: function (response, opts) {
                                        var data = Ext.decode(response.responseText).data;
                                        //record.set('eleid', data.eleid);
                                        win.ownerCt.eleid = data.eleid;
                                        // win.ownerCt.mediaUrl = win.ownerCt.isAdd?win.ownerCt.mediaUrl +win.ownerCt.eleid:win.ownerCt.mediaUrl;
                                        if(win.ownerCt.isAdd){
                                            win.uploadFrame(win.ownerCt.eleid);
                                        }else{
                                            win.uploadFrame();
                                        }
                                    }
                                });
                            }else{
                                XD.msg(response.msg)
                            }
                        }else{
                            Ext.Ajax.request({
                                method: 'POST',
                                url: win.ownerCt.selUploadMediaUrl+ '/' + file.name + '/',
                                success: function (response, opts) {
                                    var data = Ext.decode(response.responseText).data;
                                    //record.set('eleid', data.eleid);
                                    win.ownerCt.eleid = data.eleid?data.eleid:"";
                                    // win.ownerCt.mediaUrl = win.ownerCt.isAdd?win.ownerCt.mediaUrl +win.ownerCt.eleid:win.ownerCt.mediaUrl;
                                    if(win.ownerCt.isAdd){
                                        win.uploadFrame(win.ownerCt.eleid);
                                    }else{
                                        win.uploadFrame();
                                    }
                                }
                            });
                        }
                        Ext.Msg.hide();
                        XD.msg('上传成功(若为采集或管理上传原文请等待压缩完毕再进行查看)');
                    });

                    if(!win.ownerCt.isAdd){
                        win.uploadFrame();
                    }
                }
            }
        }, {
            region: 'center',
            // html: '<iframe id="mediaFrame" src="" width="100%" height="100%" style="border:0px;"></iframe>'
            html: '<iframe id="mediaFrame" src="" width="100%" height="100%" style="border:0px;"></iframe>'
        }];
        this.callParent();
    }

});