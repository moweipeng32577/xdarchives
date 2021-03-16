/**
 * Created by RonJiang on 2017/11/28 0028.
 */
Ext.define('Comps.view.MediaFormView', {
    entrytype: 'capture',//条目类型
    extend: 'Ext.panel.Panel',
    xtype: 'mediaFormView',
    itemId: 'mediaFormView',
    layout: 'border',
    flowplayer: flowplayer,
    audioPlay: audioPlay,
    acceptMedia: {},//接收文件类型
    uploadLabel: '',//上传按钮标签
    photoType:'',
    items: [],
    buttons: [
        {
            xtype: 'label',
            itemId: 'MtotalText',
            hidden:true,
            text: '',
            style: {color: 'red'},
            margin: '6 2 5 4'
        }, {
            xtype: 'label',
            itemId: 'MnowText',
            hidden:true,
            text: '',
            style: {color: 'red'},
            margin: '6 2 5 6'
        }, {
            xtype: 'button',
            itemId: 'MpreBtn',
            hidden:true,
            text: '上一条'
        }, {
            xtype: 'button',
            itemId: 'MnextBtn',
            hidden:true,
            text: '下一条'
        },
        {
        text:'批量上传',
        itemId: 'batchUploadBtn',
        width: 100,
        style: 'background-color:#00b7ee !important',
        handler:function (view) {
            var mediaFormView = view.findParentByType('mediaFormView');
            var form = mediaFormView.down('[itemId=dynamicform]');
            /*if(!form.isValid()){
                return;
            }*/
            var batchUploadView = Ext.create('Comps.view.BatchUploadView');
            batchUploadView.mediaFormView = mediaFormView;
            mediaFormView.batchUploadView = batchUploadView;
            batchUploadView.uploadCount = 0;
            batchUploadView.Messagebox = Ext.MessageBox;
            mediaFormView.isthat = window.thatcontroller;
            mediaFormView.XD = XD;
            batchUploadView.title = view.text;
            batchUploadView.show();
        }
    // },{
    //     xtype: 'displayfield',
    //     id: 'mediaPicker',
    //     itemId: 'uploadBtn'
    },
        //     {
        //     text: '连续保存',
        //     itemId: 'continueSave',
        //     width: 100
        // },
        {
            text:'删除',
            itemId: 'captureDeleteBtn',
            width: 100,
            hidden:true,
            style:"background-color:#DC143C !important",
            listeners:{
                click:function (btn) {
                    var _this = this;
                    XD.confirm("正在进行删除操作，请确认是否继续？",function(){
                        var mediaFormView = btn.findParentByType('mediaFormView');
                        var captureDataView = mediaFormView.down('[itemId=captureMediasViewExtend]');//缩略图的view
                        var deleteFiles = captureDataView.delete();//删除选择的缩略图,返回删除的文件名
                        if(mediaFormView.uploadFile == null) {return;}//上传数组判空
                        // var tempFileArray = mediaFormView.uploadFile;    //启用临时空间，防止删除upload对象时元素移位导致删除漏项
                        for(var i = 0, len = deleteFiles.length; i < len; i ++) {
                            var delIndex = mediaFormView.uploadFileIndex.indexOf(encodeURIComponent(deleteFiles[i]));
                            if(delIndex < 0){//如果文件不在勾选的记录中，则跳过
                                continue;
                            }
                            mediaFormView.uploadFile.splice(delIndex,1); //删除上传的文件信息
                            mediaFormView.uploadFileIndex.splice(delIndex,1); //删除上传的文件的索引

                        }
                    });

                }
            }
        },
        {
            text: '保存',
            itemId: 'save',
            width: 100
        }, {
            itemId: 'mediaBack',
            width: 100,
            text: '返回'
        }],
    listeners: {
        render: function (win) {
            win.newPhotoView = function (src) {
                new PhotoView({eleid: 'mediaDiv', src: src});
            };
            if (win.items.length === 0) {
                return;//初始时
            }
            var form = win.down('[itemId=dynamicform]');
            form.entryid = win.entryid;//初始化时外层id赋给内层
            var acceptType = win.acceptMedia;
            win.currentMD5 = '';//重置
            window.parent.captureFormAndGrid = win.up('acquisitionFormAndGrid');//挂在desktop.js下
            win.uploader = WebUploader.create({
                swf: '/js/Uploader.swf',//swf文件路径
                server: typeof(form.entryid) == 'undefined' ? '/electronic/serelectronics/'+win.photoType+'/' + win.entrytype : '/electronic/serelectronics/'+win.photoType+'/' + win.entrytype + "/" + form.entryid,
                pick: {
                    id: '#mediaPicker',
                    label: '&nbsp;&nbsp;&nbsp;&nbsp;' + win.uploadLabel + '&nbsp;&nbsp;&nbsp;&nbsp;'
                },
                multiple: false,//单选
                chunked: true,//是否要分片处理大文件上传(断点续传)
                chunkSize: 5242880,//文件分片大小,3M
                chunkRetry: 3,//某个分片由于网络问题出错,自动重传次数
                threads: 3,//上传并发数
                fileSingleSizeLimit: 20485760000,//单文件大小限制,500M hong:19G
                fileNumLimit:100,//上传的总数量为100个
                accept: win.acceptMedia,
                compress:false
            });

            if ("Images"==win.acceptMedia.title) {
                win.uploader.options.fileSingleSizeLimit = 157286400;//判断是否为图片 ，是的话只能上传150M之内
            }
            win.uploader.on('filesQueued', function (file) {
                if(file.length==0) {
                    return;
                }
                Ext.MessageBox.wait('正在上传，请耐心等待……', '正在操作');
                win.uploader.upload();
            });
            win.uploader.on('uploadPropress', function (file, progress) {
            });
            win.uploader.on('uploadSuccess', function (file, response) {
                if(typeof response._raw!=='undefined'&&response._raw==='false'){
                    XD.msg('未发现拍摄日期，请检查该文件是否为数码文件');
                    Ext.MessageBox.close();
                    return;
                }
                if (typeof(form.entryid) !== 'undefined' && form.entryid !== "") {
                    win.flag = true;//返回需要刷新
                }
                // if (win.mediaType.indexOf('照片') !== -1) {
                //     var imgWidth = 250;
                //     var imgHeight = 300;
                //     if (file._info) {
                //         imgWidth = file._info.width;
                //         imgHeight = file._info.height;
                //     }
                //     win.uploader.makeThumb(file, function (error, src) {
                //         if (error) {
                //             Ext.msg('不能预览');
                //         } else {
                //             win.photoView = new PhotoView({eleid: 'mediaDiv', src: src, initWidth: '90%'});
                //         }
                //     }, imgWidth, imgHeight);
                // }

                win.uploader.md5File(file).then(function (val) {
                    win.currentMD5 = val;
                    Ext.Ajax.request({
                        method: 'POST',
                        params: {isMedia: true, currentMD5: win.currentMD5},
                        url: '/electronic/electronics/' + win.entrytype + (typeof(form.entryid) == 'undefined' ? '' : ('/' + form.entryid)) + '/' + encodeURIComponent(file.name) + '/',
                        timeout:3600000,
                        async: false,
                        success: function (response) {
                            XD.msg('上传成功');
                            var data = Ext.decode(response.responseText).data;
                            var formview = form;
                            form.load({
                                url:'/electronic/getMediaData',
                                params:{
                                    filename:file.name,
                                    lastModifiedDate:file.lastModifiedDate,
                                    eleid:data.eleid
                                },
                                success:function(form,action){
                                    var filedate = action.result.msg;
                                    // if(win.addHistory){
                                    //     formview.down('[name=filedate]').setValue(filedate);
                                    // }
                                    $("textarea[name='title']").val(file.name);
                                    $("input[name='filedate']").val(filedate);
                                },
                                failure: function () {
                                    XD.msg('获取元信息失败');
                                }
                            });
                            if (!win.destroyed) {
                                win.eleid = data.eleid;
                                win.filepath = data.filepath;
                                // win.down("toolbar").query('button')[0].show();
                                win.down("toolbar").query('button')[2].show();
                                win.down('[itemId=batchUploadBtn]').hide();
                                // if (win.mediaType.indexOf('视频') !== -1|| win.mediaType.indexOf('音频') !== -1) {
                                win.compressing = true;//表示：正在压缩中的界面
                                var shape = 'width:60%"/>';
                                if (win.mediaType.indexOf('音频') !== -1) {
                                    shape = 'height:100%"/>';
                                    win.down('[itemId=mediaDetailViewItem]').expand();
                                }
                                var videoHtml = '<img src="/img/defaultMedia/videoloading.gif" style="position:absolute;top:0;right:0;left:0;bottom:0;margin:auto;' + shape;
                                document.getElementById('mediaDiv').innerHTML = videoHtml;
                                // }
                            }
                            Ext.MessageBox.close();
                        },
                        failure: function () {
                            XD.msg('操作失败！');
                            Ext.MessageBox.close();
                        }
                    });
                });
            });
            win.uploader.on('uploadError', function (file, reason) {
                Ext.MessageBox.close();
                console.log(reason);
            });
            win.uploader.on('error', function (type) {
                if (type === "Q_EXCEED_NUM_LIMIT") {
                    XD.msg('超过限制数量');
                } else if (type === "Q_TYPE_DENIED") {
                    XD.msg('所选文件与限制格式不符，限制为：' + acceptType.extensions);
                } else if (type === "Q_EXCEED_SIZE_LIMIT") {
                    XD.msg('超过限制的文件大小！')
                } else  if(type==="F_EXCEED_SIZE"){
                    XD.msg("图片不允许大小超过150M或者其他文件不允许超过500M");
                }else{
                    XD.msg(type);
                }
                Ext.MessageBox.close();
            });
            win.uploader.on('uploadComplete', function () {
            });
            win.uploader.on('uploadFinished', function () {
                // XD.msg('所有文件上传完毕');
            });
        }
    }
});


//文件上传弹出框
Ext.define('Comps.view.BatchUploadView', {
    extend: 'Ext.window.Window',
    xtype: 'batchUploadView',
    uploader: null,
    modal: true,
    width: 800,
    height: 400,
    title: '批量上传',
    layout: 'fit',
    closeToolText: '关闭',
    // closable:false, //去除右上角关闭按钮
    actions: {
        del: {
            iconCls: 'x-action-upload-delete-icon',
            tooltip: '删除',
            handler: function (view, row) {
                var grid = view.grid;
                var record = grid.getStore().getAt(row);
                //从上传队列中删除文件
                var uploader = grid.findParentByType('batchUploadView').uploader;
                uploader.removeFile(record.get('id'), true);
                //从列表中删除文件
                grid.getStore().remove(record);
                //清除服务器上的缓存
                Ext.Ajax.request({
                    method: 'DELETE',
                    url: '/electronic/chunk/' + record.get('name') + '/'
                });
            },
            getClass: function(v, metadata, r, rowIndex, colIndex, store) {
                if(typeof(r.data.eleid) != 'undefined') {
                    return "x-hidden";
                }else{
                    return "x-action-upload-delete-icon";
                }
            }
        }
    },
    items: {
        xtype: 'grid',
        store: [],
        border: false,
        scrollable: true,
        columns: [{xtype: 'rownumberer'}, {hidden: true, dataIndex: 'id'}, {
            header: '文件名称',
            dataIndex: 'name',
            flex: 1
        }, {
            text: '文件大小',
            dataIndex: 'size',
            width: 80,
            renderer: function (value) {
                return value + 'MB';
            }
        }, {
            text: '上传进度',
            xtype: 'widgetcolumn',
            width: 150,
            widget: {
                bind: '{record.progress}',
                xtype: 'progressbarwidget',
                textTpl: ['{percent:number("1")}%']
            }
        }, {
            xtype: 'actioncolumn',
            width: 30,
            items: ['@del']
        }],
        tbar: [{
            xtype: 'displayfield',
            id: 'picker',
            width: 82,
            height: 37
        }, {
            xtype: 'button',
            text: '开始上传',
            width: 90,
            height: 37,
            handler: function () {
                var win = this.findParentByType('batchUploadView');
                if (win.down('grid').getStore().getCount() == 0) {
                    XD.msg('未选择文件');
                    return;
                }
                var captureDataView = win.mediaFormView.down('[itemId=captureMediasViewExtend]');//缩略图的view
                // if(win.fileCount == null || win.fileCount == 'undefined')
                //     win.fileCount = win.mediaFormView.uploadFile == null ? 0 : win.mediaFormView.uploadFile.length;//令上传成功的计数为0
                if(captureDataView.size() == 0)
                {
                    captureDataView.cleanList();//清空缩略图列表
                    sessionStorage.setItem("ishasCaptureUpload","0");//标识第一次上传
                }
                else {
                    sessionStorage.setItem("ishasCaptureUpload","1");//标识再次上传
                }
                win.mediaFormView.down("toolbar").query('button')[2].show();
                // Ext.MessageBox.wait('正在上传文件，并获取文件信息......');
                win.uploader.upload();

                win.uploader.notDigitFile ='';//采集数码照片时，所上传的照片不是数码照片的集合

                win.tools[0].hide();//隐藏弹窗关闭按钮，防止上传人为中断导致保存按钮失效
            }
        }]
    },
    listeners: {
        render: function (win) {
            //注册分片上传处理事件，用于断点续传
            WebUploader.Uploader.register({
                name: 'electronic',
                'before-send': 'checkchunk'
            }, {
                checkchunk: function (block) {
                    var deferred = WebUploader.Deferred();
                    Ext.Ajax.request({
                        url: '/electronic/chunk/' + encodeURIComponent(block.file.name) + '/' + block.chunks + '/' + block.chunk,
                        async: false,
                        success: function (response, opts) {
                            if (response.responseText == "true") {
                                deferred.reject();
                            } else {
                                deferred.resolve();
                            }
                        }
                    });
                    return deferred.promise();
                }
            });
            //初始化文件上传组件
            win.uploader = WebUploader.create({
                // swf文件路径
                swf: '/js/Uploader.swf',
                // 文件接收服务端。
                server: '/electronic/serelectronicsCapture/'+win.mediaFormView.photoType+"/"+win.mediaFormView.entrytype + '/' + win.mediaFormView.mediaType ,
                // 选择文件的按钮。可选。
                // 内部根据当前运行是创建，可能是input元素，也可能是flash.
                pick: {id: '#picker', label: '选择文件'},
                //是否要分片处理大文件上传(断点续传)
                chunked: true,
                //大图片是否进行压缩
                compress:false,
                //文件分片大小，5M
                chunkSize: 5242880,
                //某个分片由于网络问题出错，自动重传次数
                chunkRetry: 3,
                //上传并发数
                threads: 3,
                //单文件大小限制，19G
                fileSingleSizeLimit: 20485760000,
                //文件类型
                accept: win.mediaFormView.acceptMedia,
                //上传的总数量为100个
                fileNumLimit:100
            });
            if ("Images"==win.mediaFormView.acceptMedia.title) {
                win.uploader.options.fileSingleSizeLimit = 157286400;//判断是否为图片 ，是的话只能上传150M之内
            }
            //监听文件选择事件，将选中的文件信息添加到列表中
            win.uploader.on('filesQueued', function (files) {
                // win.hasFile = files.length; //记录一共有多少分文件
                var msg = '';
                for (var i = 0; i < files.length; i++) {
                    if(win.mediaFormView.uploadFileIndex != null ){
                        var index = win.mediaFormView.uploadFileIndex.indexOf(files[i].name);//判断文件是否已经上传
                        if(index >= 0) {//文件已经上传
                            if(msg == null || msg == '')
                                msg += files[i].name
                            else
                                msg += "," + files[i].name;
                            win.uploader.removeFile(files[i].id);
                            // files.splice(i,1); //删除上传的文件信息
                            // i--;
                            continue;
                        }
                    }

                    win.down('grid').getStore().add({
                        id: files[i].id,
                        name: files[i].name,
                        size: Math.floor(files[i].size / 10240) / 100,
                        progress: 0
                    });
                }
                if(msg != null && msg != '')
                    XD.msg( "以下文件因重复无法上传："+ msg);
            });
            //监听文件上传进度，更新列表中上传进度条
            win.uploader.on('uploadProgress', function (file, progress) {
                if (!win.destroyed) {
                    var record = win.down('grid').getStore().getById(file.id);
                    record.set('progress', progress);
                }
            });
            //监听文件上传成功，提示用户
            win.uploader.on('uploadSuccess', function (file, response) {
                console.log("上传单份文件监听");
                if(response != true){
                    win.uploader.removeFile(file.id);//采集数码照片时，上存的是历史照片则在队列中移除该文档。
                    win.uploader.notDigitFile = win.uploader.notDigitFile + file.name + ',';
                    return;
                }

                var form = win.mediaFormView.down('[itemId=dynamicform]');
                win.uploader.md5File(file).then(function (val) {
                    win.currentMD5 = val;
                });
                var fieldCode = form.getRangeDateForCode();//字段编号，用于特殊的自定义字段(范围型日期)
                var nodeid = form.nodeid;
                var nodename = getNodename(nodeid);
                var addHistory;
                if ( win.mediaFormView != null && win.mediaFormView.addHistory) {
                    addHistory = "addHistory";
                }
                //表单的数据
                var setcode = "";//addCalValue(form); //档号最后一位组成
                var FileJson = {
                    nodeid:nodeid,
                    nodename:nodename,
                    filename:encodeURIComponent(file.name), //文件名
                    lastModifiedDate:file.lastModifiedDate,//最后修改日期
                    currentMD5:win.currentMD5,          //文件MD5
                    setcode:setcode,            //文件档号
                    // count:count,                //当前文件在上传文件列表中的索引
                    //archivecodeSetState:form.setArchivecodeValueWithNode(nodename), //后期不用要的要屏蔽
                    fieldCode:fieldCode,
                    addHistory:addHistory
                }
                var StringFileJson = JSON.stringify(FileJson);
                if(win.mediaFormView.uploadFile == null) {
                    win.mediaFormView.uploadFile = [];
                    win.mediaFormView.uploadFileIndex = []; //上传文件数组的索引,放文件名
                    win.mediaFormView.uploadFile.push(FileJson);
                    win.mediaFormView.uploadFileIndex.push(FileJson.filename);
                }
                else
                {
                    win.mediaFormView.uploadFile.push(FileJson);  //上传文件的每份数据
                    win.mediaFormView.uploadFileIndex.push(FileJson.filename);
                }
            });
            win.uploader.on('error', function (type) {
                if (type === "Q_EXCEED_NUM_LIMIT") {
                    XD.msg('超过限制数量');
                } else if (type === "Q_TYPE_DENIED") {
                    XD.msg('所选文件与限制格式不符，限制为：' + win.mediaFormView.acceptMedia.extensions);
                } else if (type === "Q_EXCEED_SIZE_LIMIT") {
                    XD.msg('超过限制的文件大小！')
                } else if(type==="F_EXCEED_SIZE"){
                    XD.msg("图片不允许大小超过150M或者其他文件不允许超过5000M");
                }else if(type === "F_DUPLICATE"){
                    XD.msg("文件有重复");
                }
                else{
                    XD.msg(type);
                }
                // Ext.MessageBox.close();
            });
            //监听所有文件上传完毕
            win.uploader.on('uploadFinished', function () {
                console.log("上传所有文件监听");
                // var valAddBtn = win.mediaFormView.down("toolbar").query('button')[2]; //采集时的保存按钮
                var valAddBtn = win.mediaFormView.down('[itemId=save]'); //采集时的保存按钮
                valAddBtn.show();
                var captureDataView = win.mediaFormView.down('[itemId=captureMediasViewExtend]');//缩略图的view
                var mediaFormView = win.mediaFormView;
                var uploadList = mediaFormView.uploadFile//要上传的每份文件的对象数组
                if(win.uploader.notDigitFile != ''){
                    XD.msg('所有上存的文件，其中,'+win.uploader.notDigitFile +' 不是数码照片');
                }
                if(uploadList == undefined){
                    return;
                }
                // console.log("上传的文件数量 : " + mediaFormView.uploadFile.length);
                // console.log("上传的对象数组：" + uploadList);
                if(sessionStorage.getItem("ishasCaptureUpload") == null || sessionStorage.getItem("ishasCaptureUpload") == "0"){     //判断是否是第一次上传
                    sessionStorage.setItem("ishasCaptureUpload","1");
                    valAddBtn.on('click', function () {
                        var form = mediaFormView.down('[itemId=dynamicform]');
                        if(!form.isValid()){
                            XD.msg('有必填项未填写');
                            return;
                        }
                        var operateFlag = mediaFormView.operateFlag;
                        // var uploadMediaType = mediaFormView.mediaType;
                        var entrytype = mediaFormView.entrytype;
                        // var uploadMap = win.fileDateMap;    //将放日期的Map传过来
                        for(var i = 0; i < mediaFormView.uploadFile.length; i++) {
                            // var fileJsonStringUpload = sessionStorage.getItem('UploadFile_' + uploadMediaType + '_' + operateFlag + '_' + i);
                            // if(fileJsonStringUpload == null || fileJsonStringUpload == 'undefined') continue;
                            // var fileJsonUpload = JSON.parse(fileJsonStringUpload);
                            if(uploadList[i] == null || uploadList == 'undefined') continue;
                            var fileJsonUpload = uploadList[i];//上传的文件信息
                            var labels = [];
                            var params = {
                                nodeid: fileJsonUpload.nodeid,
                                type: operateFlag,
                                operate: fileJsonUpload.nodename,
                                isMedia: true,
                                addHistory: fileJsonUpload.addHistory,
                                lastModifiedDate: new Date(),//uploadMap.get(fileJsonUpload.filename + '_' + i),
                                currentMD5: fileJsonUpload.currentMD5,
                                setcode:fileJsonUpload.setcode,//档号
                                filenameLab:typeof(window.CaptureItem.options.items.get(decodeURI(fileJsonUpload.filename)))!="undefined"?window.CaptureItem.options.items.get(decodeURI(fileJsonUpload.filename))["setLabel"]:labels //缩略图对应的标签id
                            };
                            if (fileJsonUpload.fieldCode != null) {
                                params[fieldCode] = fileJsonUpload.fieldCode;
                            }
                            form.submit({
                                method: 'POST',
                                url: '/electronic/batchUploadSave/' + entrytype + "/" + fileJsonUpload.filename + "/",
                                params: params,
                                // timeout: 3600000,
                                async: false,
                                success: function (form, action) {
                                    if (!win.destroyed) {
                                        var grid = win.down('grid');
                                        var record = grid.getStore().getById(file.id);
                                        grid.getView().getRow(record).style.backgroundColor = '#87CEFA';
                                    }
                                    mediaFormView.isthat.activeGrid(mediaFormView, true);
                                    mediaFormView.isthat.viewAndListFresh(mediaFormView);
                                    mediaFormView.destroy();
                                },
                                failure: function (form, action) {
                                    XD.msg('操作失败！');
                                }
                            });

                        }
                        // win.fileCount = 0;
                        XD.msg("采集任务已提交成功，后台正努力压缩，您可以继续进行批量采集.");
                        captureDataView.cleanList();//清空缩略图列表
                        mediaFormView.uploadFile = null;//删除上传的文件
                        mediaFormView.uploadFileIndex = null;
                    },valAddBtn);
                }
                // Ext.MessageBox.close();
                XD.msg('所有文件上传完毕');
                var batchUploadView = win.mediaFormView.batchUploadView;
                batchUploadView.close();
            });
        },
        beforeclose: function (win) {
            win.uploader.stop(true);
            win.uploader.reset();
            win.uploader.destroy();
            WebUploader.Uploader.unRegister('inform');
        }
    }
});

function getNodename(nodeid) {
    var nodename;
    Ext.Ajax.request({
        async:false,
        url: '/nodesetting/getFirstLevelNode/' + nodeid,
        success:function (response) {
            nodename = Ext.decode(response.responseText);
        }
    });
    return nodename;
}

//从页面获取计算项数据并+count
function addCalValue(form) {
    var codeSettingFieldList;//档号构成字段的集合
    Ext.Ajax.request({//获得档号构成字段的集合
        url:'/codesetting/getCodeSettingFields',
        async:false,
        params:{
            nodeid:form.nodeid
        },
        success:function(response){
            codeSettingFieldList = Ext.decode(response.responseText).data;
        }
    });
    if(!codeSettingFieldList){
        XD.msg('请检查档号设置信息是否正确');
        return;
    }
    var calFieldName = codeSettingFieldList[codeSettingFieldList.length-1];
    return calFieldName;
}