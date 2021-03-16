/**
 * Created by RonJiang on 2017/11/28 0028.
 */
Ext.define('Comps.view.EntryMediaFormView',{
    extend:'Ext.tab.Panel',
    xtype:'EntryMediaFormView',
    itemId:'EntryMediaFormView',
    //标签页靠左配置--start
    tabPosition:'top',
    tabRotation:0,
    //标签页靠左配置--end
    nodeid:'',//节点
    mediaTitle:'原文',//原文类型
    dataUrl:'',//数据链接
    mediaUrl:'',//原文链接
    uploadUrl:'',//文件上传链接
    mediaType:'',//文件上传现在类型
    selUploadMediaUrl:'',//上传完电子文件条目链接
    isDigital:false,//是否为数码照片
    activeTab:0,
    isLook:false,//判断是会否为查看权限

    initComponent: function() {
        var me = this;
        me.items = [{
            title:'条目',
            iconCls: 'x-tab-entry-icon',
            itemId:'dynamicform',
            xtype:'dynamicform',
            nodeid:me.nodeid,
            dataUrl:me.dataUrl,
            items:[{
                xtype:'hidden',
                name:'entryid'
            }],
            bbar:['->',{
                    text:'保存',
                    itemId:'lsEditSave',
                    width: 100,
                    height: 37,
                    style:{'background-color':'white'},
                    hidden:me.isLook
                },
                {
                    xtype: 'button',
                    itemId:'lsEditBack',
                    width: 100,
                    height: 37,
                    text: '返回',
                    style:{'background-color':'white'}
                }]
        },{
            title:me.mediaTitle,
            iconCls:'x-tab-electronic-icon',
            itemId:'electronic',
            entrytype:'electronic',
            xtype:'mediaView',
            isTop:false,
            isAdd:me.isAdd,
            mediaUrl:me.mediaUrl,
            uploadUrl:me.uploadUrl,
            mediaType:me.mediaType,
            selUploadMediaUrl:me.selUploadMediaUrl,
            isLook:me.isLook,
            isDigital:me.isDigital
        }];

        // me.buttons = [{
        //     text:'保存',
        //     itemId:'back'
        // }];

        this.callParent();
    }
});

// Ext.define('Comps.view.mediaView', {
//     extend: 'Ext.panel.Panel',
//     xtype: 'mediaView',
//     layout: 'border',
//     bodyBorder: false,
//     initComponent: function() {
//         var me = this;
//         me.items = [{
//             region: 'south',
//             bbar: ['->',{
//                 //此处实现文件选择
//                 xtype: 'displayfield',
//                 height: 37,
//                 id: 'picker',
//                 hidden:me.isLook
//             }, {
//                 xtype: 'button',
//                 itemId:'mediaViewBack',
//                 width: 100,
//                 height: 37,
//                 text: '返回',
//                 style:{'background-color':'white'}
//             }],
//
//             uploadFrame:function(){
//                 var mediaFrame = document.getElementById('mediaFrame');
//                 mediaFrame.setAttribute('src',  this.ownerCt.mediaUrl);
//             },
//
//             listeners: {
//                 render: function (win) {
//                     //console.log(win.mediaUrl);
//                     //初始化文件上传组件
//                     win.uploader = WebUploader.create({
//                         //swf文件路径
//                         swf: '/js/Uploader.swf',
//                         //文件接收服务端
//                         server: win.ownerCt.uploadUrl,
//                         //选择文件的按钮,可选
//                         //内部根据当前运行是创建,可能是input元素,也可能是flash
//                         pick: {
//                             id: '#picker',
//                             label: '选择'
//                         },
//                         //是否要分片处理大文件上传(断点续传)
//                         chunked: true,
//                         //文件分片大小,5M
//                         chunkSize: 5242880,
//                         //某个分片由于网络问题出错,自动重传次数
//                         chunkRetry: 3,
//                         //上传并发数
//                         threads: 3,
//                         //单文件大小限制,500M
//                         fileSingleSizeLimit: 52428800,
//                         accept: win.ownerCt.mediaType
//                     });
//                     //监听文件选择时间,将选中的文件信息添加到列表中
//                     win.uploader.on('filesQueued', function () {
//                         //上传文件
//                         win.uploader.upload();
//                     });
//                     //监听文件上传进度,更新列表上传进度条
//                     win.uploader.on('uploadPropress', function (file, progress) {
//                         //todo
//                     });
//                     //监听文件上传成功,提示用户
//                     win.uploader.on('uploadSuccess', function (file, response) {
//                         Ext.Ajax.request({
//                             method: 'POST',
//                             url: win.ownerCt.selUploadMediaUrl+ '/' + file.name + '/',
//                             success: function (response, opts) {
//                                 var data = Ext.decode(response.responseText).data;
//                                 //record.set('eleid', data.eleid);
//                                 win.uploadFrame();
//                             }
//                         });
//
//
//                     });
//
//                     win.uploadFrame();
//                 }
//             }
//         }, {
//             region: 'center',
//             // html: '<iframe id="mediaFrame" src="" width="100%" height="100%" style="border:0px;"></iframe>'
//             html: '<iframe id="mediaFrame" src="" width="100%" height="100%" style="border:0px;"></iframe>'
//         }];
//         this.callParent();
//     }
//
// });
