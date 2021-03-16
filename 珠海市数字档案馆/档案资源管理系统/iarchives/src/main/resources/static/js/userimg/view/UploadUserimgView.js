/**
 * Created by huamx on 2018/03/14
 */
Ext.define('Userimg.view.UploadUserimgView', {
    extend: 'Ext.panel.Panel',
    xtype: 'UploadUserimgView',
    layout: 'border',
    bodyBorder: false,
    itemId: 'UploadUserimgViewId',
    items: [{
        region: 'south',
        bbar: [{
            //此处实现文件选择
            xtype: 'displayfield',
            height: 37,
            id: 'picker'
        }, {
            xtype: 'button',
            width: 385,
            height: 37,
            style:{'background-color':'#708090'},
            text: '关闭',
            handler: function () {
                //关闭窗口
                parent.closeObj.close(parent.layer.getFrameIndex(window.name));
            }
        }],
        listeners: {
            render: function (win) {
                //初始化文件上传组件
                win.uploader = WebUploader.create({
                    //swf文件路径
                    swf: '/js/Uploader.swf',
                    //文件接收服务端
                    server: '/electronic/electronicsUserimg',
                    //选择文件的按钮,可选
                    //内部根据当前运行是创建,可能是input元素,也可能是flash
                    pick: {
                        id: '#picker',
                        label: '更换头像'
                    },
                    //是否要分片处理大文件上传(断点续传)
                    chunked: true,
                    //文件分片大小,5M
                    chunkSize: 5242880,
                    //某个分片由于网络问题出错,自动重传次数
                    chunkRetry: 3,
                    //上传并发数
                    threads: 3,
                    //单文件大小限制,500M
                    fileSingleSizeLimit: 52428800,
                    accept: {
                        title: 'Images',
                        extensions: 'jpg,jpeg,png',
                        miniTypes: 'image/*'
                    }
                });
                //监听文件选择时间,将选中的文件信息添加到列表中
                win.uploader.on('filesQueued', function () {
                    //上传文件
                    win.uploader.upload();
                });
                //监听文件上传进度,更新列表上传进度条
                win.uploader.on('uploadPropress', function (file, progress) {
                    //todo
                });
                //监听文件上传成功,提示用户
                win.uploader.on('uploadSuccess', function (file, response) {
                    Ext.Ajax.request({
                        url: '/electronic/saveUserimgInfo',
                        params: {
                            filename: file.name
                        },
                        method: 'POST',
                        success: function (res, opt) {
                            var mediaFrame = document.getElementById('mediaFrame');
                            mediaFrame.setAttribute('src', '/electronic/showUserimg');
                            window.parent.closeObj.changeHeadPortrait('/electronic/outputUserimg');
                        },
                        failure: function () {
                            // XD.msg('操作失败');
                        },
                        scope: this
                    });
                });
            }
        }
    }, {
        region: 'center',
        html: '<iframe id="mediaFrame" src="" scrolling="no" style="width:360px;height:360px;border-radius: 50%;margin-top: 70px;margin-left: 70px"></iframe>'
    }]
});
