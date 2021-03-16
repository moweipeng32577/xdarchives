/**
 * Created by Administrator on 2018/11/28.
 */
var dataSourceType="";
Ext.define('Borrowfinish.controller.BorrowFinishController', {
    extend: 'Ext.app.Controller',
    views: ['BorrowFinishGridView','BorrowFinishDetailGridView','BorrowFinishDetailView','BorrowFinishDealGridView',
    'BorrowFinishElectronicView','BorrowFinishSolidView'],//加载view
    stores: ['BorrowFinishGridStore','BorrowFinishDetailGridStore','BorrowFinishDealGridStore'],//加载store
    models: ['BorrowFinishGridModel','BorrowFinishDetailGridModel','BorrowFinishDealGridModel'],//加载model
    init: function () {
        var borrowfinish;
        this.control({
            'borrowFinishGridView': {
                afterrender: function (view) {
                    view.initGrid();
                    window.borrowfinishGridView = view;

                },
                itemclick:function (view,select) {
                   var borrowdocid = select.get('id');
                   var state = select.get('state');
                   var type = select.get('type');
                   dataSourceType=select.get('datasourcetype');
                   if(state=='已通过'){
                       var borrowFinishDetailView = Ext.create('Borrowfinish.view.BorrowFinishDetailView');
                       var borrowFinishDetailGridView = borrowFinishDetailView.down('borrowFinishDetailGridView');
                       if (type == '实体查档') {
                           borrowFinishDetailGridView.down('[itemId=lookMedia]').hide();
                       }else{
                           borrowFinishDetailGridView.down('[itemId=reItem]').hide();//隐藏未归还状态列
                       }
                       borrowFinishDetailGridView.initGrid({borrowdocid: borrowdocid});
                       Ext.Ajax.request({
                           url: '/indexly/setBorrowFinish',
                           async: false,
                           params: {
                               borrowdocid: borrowdocid
                           },
                           success: function (response) {
                           },
                           failure: function () {
                               XD.msg('操作失败');
                           }
                       });
                           window.docid = borrowdocid;
                           borrowFinishDetailView.show();
                   }else{
                       var dealDetailsWin = Ext.create('Ext.window.Window',{
                           modal:true,
                           width:1000,
                           height:530,
                           title:'办理详情',
                           layout:'fit',
                           closeAction:'hide',
                           listeners: {'close':function(){
                               window.borrowfinishGridView.getStore().reload();
                               window.borrowfinishGridView.getSelectionModel().clearSelections();
                           }
                           },
                           items:[{
                               xtype: 'borrowFinishDealGridView'
                           }]
                       });
                       var store = dealDetailsWin.down('borrowFinishDealGridView').getStore();
                       store.proxy.extraParams.borrowdocid = borrowdocid;
                       window.borrowdocid = borrowdocid;
                       store.reload();
                       Ext.Ajax.request({
                           url: '/indexly/setBorrowFinish',
                           async: false,
                           params: {
                               borrowdocid: borrowdocid
                           },
                           success: function (response) {
                           },
                           failure: function () {
                               XD.msg('操作失败');
                           }
                       });
                       dealDetailsWin.show();
                   }
                }
            },
            'borrowFinishDealGridView button[itemId=lookApproveId]': {//查看单据批示
                click: function () {
                    var borrowdocid = window.borrowdocid;
                    var approve = Ext.create("Ext.window.Window", {
                        width: 370,
                        height: 210,
                        title: '查看批示',
                        modal: true,
                        closeToolText: '关闭',
                        items: [{
                            xtype: 'form',
                            defaults: {
                                layout: 'form',
                                xtype: 'container',
                                defaultType: 'textarea'
                            },
                            items: [{
                                itemId: 'approveId',
                                xtype: 'textarea',
                                name: 'approve',
                                margin: '15',
                                flex: 2,
                                readOnly: true,
                                width: 340,
                                height: 130
                            }]
                        }]
                    }).show();

                    approve.down('form').load({
                        url: '/jyAdmins/getApprove',
                        params: {borrowdocid: borrowdocid},
                        success: function (form, action) {
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            'mediaFormView [itemId=mediaBack]': {//查看返回
                click: this.lookBack
            },
            'borrowFinishDetailGridView button[itemId=lookMedia]': {
                click: function (btn) {
                    var select = btn.findParentByType('borrowFinishDetailGridView').getSelectionModel();
                    if (select.getSelected().length != 1) {
                        XD.msg('请选择一条数据');
                        return;
                    }
                    //判断是否已到期
                    if(select.getSelected().items[0].get('responsible') < getDateStr(0)){
                        XD.msg('该查档已到期，不允许查看原文！');
                        return;
                    }
                    if(select.getSelected().items[0].get('lyqx')==='拒绝'){
                        XD.msg('该数据未审批通过，不允许查看原文！');
                        return;
                    }
                    var entryid = select.getSelected().items[0].get("entryid");
                    var record=select.getSelected().items;
                    var eleids = [];
                    eleids = this.getEleids(entryid,window.docid);
                    if(eleids.length==0){
                        XD.msg('没有可以查看的原文');
                        return;
                    }
                    if(dataSourceType=="soundimage"){
                        var nodename=record[0].get('nodefullname');
                        var mediaFormView = this.getNewMediaFormView(btn,'look',nodename,record);
                        var form = mediaFormView.down('[itemId=dynamicform]');
                        var initFormFieldState = this.initFormField(form, 'hide', record[0].get('nodeid'));
                        if(!initFormFieldState){//表单控件加载失败
                            return;
                        }
                        form.operate = 'look';
                        var records = record;
                        form.entryids = entryid;
                        form.nodeids = select.getSelected().items[0].get("nodeid");
                        form.selectItem = records;
                        form.entryid = records[0].get('entryid');
                        this.initMediaFormData('look', form, records[0].get('entryid'), records[0]);
                        mediaFormView.down("[itemId=batchUploadBtn]").hide();
                        mediaFormView.down('[itemId=save]').hide();
                        if(nodename.indexOf('音频') !== -1){
                            mediaFormView.down('[itemId=mediaDetailViewItem]').expand();
                        }
                        form.up('borrowFinishDetailView').setActiveItem(mediaFormView);
                    }else {
                        var media = Ext.create("Borrowfinish.view.BorrowFinishElectronicView");
                        //初始化原文数据
                        var solidview = media.down('borrowFinishSolidView');
                        solidview.isJy = false;
                        solidview.entryid = entryid;
                        window.remainEleids = eleids;
                        var treeStore = solidview.down('treepanel').getStore();
                        if (typeof (entryid) == 'undefined') {
                            solidview.down('treepanel').getRootNode().removeAll();
                            return;
                        }
                        solidview.unionAll = true;
                        treeStore.reload();
                        media.show();
                        window.media = media;
                        Ext.on('resize', function () {
                            window.media.setPosition(0, 0);
                            window.media.fitContainer();
                        });
                    }
                }
            },
            'borrowFinishDetailGridView button[itemId=hide]': {
                click: function (btn) {
                    var borrowFinishDetailView = btn.findParentByType('borrowFinishDetailView');
                    window.borrowfinishGridView.getStore().reload();
                    window.borrowfinishGridView.getSelectionModel().clearSelections();
                    borrowFinishDetailView.close();
                }
            }
        });
    },
    getEleids:function(entryid,borrowcodeid){
        var eleids=[];
        Ext.Ajax.request({
            url:'/electronic/geteleids',
            async:false,
            params:{
                entryid:entryid,
                borrowcodeid:borrowcodeid
            },
            success:function (response) {
                eleids = Ext.decode(response.responseText);

            }
        });
        return eleids;
    },
    //获取新的mediaFormView
    getNewMediaFormView: function (btn, operate, mediaType,record) {
        var formAndGrid= btn.up("borrowFinishDetailGridView")
        var entryid = '';
        var across=record[0];
        if (typeof across !== 'undefined' && (operate === 'look' || operate === 'modify')) {
            entryid = across.get('entryid');
        }
        var accept, uploadLabel;
        var dynamicRegion = 'west', header = true, col_spli = true, collapsed = false, collapsible = false;
        if (mediaType.indexOf('照片') !== -1) {
            accept = {
                title: 'Images',
                extensions: 'jpeg,jpg,png,bmp,gif,tiff,tif,crw,cr2,nef,raf,raw,kdc,mrw,nef,orf,dng,ptx,pef,arw,x3f,rw2',
                mimeTypes: 'image/*'
            };
            uploadLabel = '上传照片';
        } else if (mediaType.indexOf('视频') !== -1) {
            accept = {
                title: 'Videos',
                extensions: 'mp4,avi',
                mimeTypes: 'video/*'
            };
            uploadLabel = '上传视频';
        } else if (mediaType.indexOf('音频') !== -1) {
            accept = {
                title: 'Audio',
                extensions: 'mp3',
                mimeTypes: 'audio/*'
            };
            uploadLabel = '上传音频';
            dynamicRegion = 'south';
            header = false;
            col_spli = false;
            collapsed = true;
            collapsible = true;
        }
        formAndGrid.remove(formAndGrid.down('[itemId=amediaFormView]'));//删除原有的mediaFormView，确保干净
        var dynamicFromItem = {
            region: dynamicRegion,
            title: '条目',
            iconCls: 'x-tab-entry-icon',
            itemId: 'dynamicform',
            xtype: 'dynamicform',
            calurl: '/management/getCalValue',
            items: [{
                xtype: 'hidden',
                name: 'entryid'
            }],
            width: '70%',
            flex: 4,
            collapsible: col_spli,
            split: col_spli
        };
        var detailViewItem = {
            region: 'center',
            header: header,
            title: uploadLabel.substr(2, 2),
            iconCls: 'x-tab-electronic-icon',
            itemId: 'mediaDetailViewItem',
            entrytype: '',
            layout: 'fit',
            xtype: 'panel',
            items: [{
                itemId: 'mediaHtml',
                html: '<div id="mediaDiv" class="pw-view" style="background:white"></div>'
            }],
            flex: 1,
            collapsed: collapsed,
            collapsible: collapsible
        };
        formAndGrid.add({
            itemId: 'amediaFormView',
            xtype: 'mediaFormView',
            entryid: entryid,
            flag: false,//默认不用刷新
            acceptMedia: accept,
            uploadLabel: uploadLabel,
            mediaType: mediaType,
            items: [dynamicFromItem, detailViewItem]
        });
        return formAndGrid.down('[itemId=amediaFormView]');
    },

    initMediaFormData: function (operate, form, entryid, record) {
        var nullvalue = new Ext.data.Model();
        form.down('[itemId=preBtn]').hide();
        form.down('[itemId=nextBtn]').hide();
        var mediaFormView = form.up('mediaFormView');
        var fields = form.getForm().getFields().items;
        var prebtn = mediaFormView.down('[itemId=MpreBtn]');
        var nextbtn = mediaFormView.down('[itemId=MnextBtn]');
        var totaltext = form.up("mediaFormView").down('[itemId=MtotalText]');
        totaltext.setText('当前共有  1  条，');
        var nowtext = form.up("mediaFormView").down('[itemId=MnowText]');
        nowtext.setText('当前记录是第  1  条');
        totaltext.show();
        nowtext.show();
        prebtn.hide();
        nextbtn.hide();
        for (var i = 0; i < fields.length; i++) {
            if (fields[i].value && typeof(fields[i].value) == 'string' && fields[i].value.indexOf('label') > -1) {
                continue;
            }
            if (fields[i].xtype == 'combobox') {
                fields[i].originalValue = null;
            }
            nullvalue.set(fields[i].name, null);
        }
        form.loadRecord(nullvalue);
        if (operate != 'look' && operate != 'lookfile') {

        } else {
            Ext.each(fields, function (item) {
                item.setReadOnly(true);
            });
        }
        var urls= '/management/entries/' + entryid+"?xtType="+"声像系统";
        Ext.Ajax.request({
            method: 'GET',
            scope: this,
            url: urls,
            success: function (response) {
                var entry = Ext.decode(response.responseText);
                var data = Ext.decode(response.responseText);
                if (data.organ) {
                    entry.organ = data.organ;//机构
                }
                var fieldCode = form.getRangeDateForCode();//字段编号，用于特殊的自定义字段(范围型日期)
                if (fieldCode != null) {
                    //动态解析数据库日期范围数据并加载至两个datefield中
                    form.initDaterangeContent(entry);
                }
                form.loadRecord({
                    getData: function () {
                        return entry;
                    }
                });
                form.entryid = entry.entryid;
                if (operate == 'look' || operate == 'modify') {
                    Ext.Ajax.request({
                        method: 'POST',
                        params: {entryid: entry.entryid},
                        url: '/electronic/getSxElectronicByEntryid',
                        async: false,
                        success: function (response) {
                            var eleRecord = Ext.decode(response.responseText).data;
                            mediaFormView.currentMD5 = eleRecord.md5;
                            if (record.get('background') === '') {
                                mediaFormView.compressing = true;
                                var videoHtml = '<img src="/img/defaultMedia/videoloading.gif" style="position:absolute;top:0;right:0;left:0;bottom:0;margin:auto;width:350px;height:240px"/>';
                                document.getElementById('mediaDiv').innerHTML = videoHtml;
                            } else if (mediaFormView.mediaType.indexOf('照片') !== -1) {
                                if (typeof(mediaFormView.photoView) == 'undefined') {
                                    mediaFormView.photoView = new PhotoView({
                                        eleid: 'mediaDiv',
                                        src: '/electronic/loadSpecialMedia?entryid=' + entryid+"&fileType=photo",
                                        initWidth: '90%'
                                    });
                                } else {
                                    Ext.apply(mediaFormView.uploader.options, {
                                        server: '/electronic/serelectronics/' + mediaFormView.entrytype + "/" + form.entryid
                                    });
                                    mediaFormView.photoView.changeImg('/electronic/loadSpecialMedia?entryid=' + entryid);
                                }
                            } else if (mediaFormView.mediaType.indexOf('视频') !== -1) {
                                mediaFormView.compressing = false;
                                var videoHtml = '<a href="/electronic/loadSpecialMedia?entryid=' + entryid + '&fileType=video" style="position:absolute;top:0;right:0;left:0;bottom:0;margin:auto;width:520px;height:320px" id="player"></a>';
                                document.getElementById('mediaDiv').innerHTML = videoHtml;
                                flowplayer("player", "../js/flowplayerFlash/flowplayer.swf", {
                                    plugins: {
                                        controls: {
                                            height: 30,
                                            tooltips: {
                                                buttons: true,
                                                play: '播放',
                                                fullscreen: '全屏',
                                                fullscreenExit: '退出全屏',
                                                pause: '暂停',
                                                mute: '静音',
                                                unmute: '取消静音'
                                            }
                                        }
                                    },
                                    canvas: {
                                        backgroundColor: '#000',
                                        backgroundGradient: [0, 0]//无渐变色
                                    },
                                    clip: {
                                        autoPlay: false,
                                        autoBuffering: true
                                    },
                                    onStart: function (clip) {
                                        animate(this, clip, {
                                            height: 320,
                                            width: 520
                                        })
                                    },
                                    onFullscreen: function (clip) {
                                        setTimeout(function () {
                                            animate(this, clip, {
                                                height: screen.height,
                                                width: screen.width
                                            }, clip);
                                        }, 1000);
                                    }
                                });
                            } else if (mediaFormView.mediaType.indexOf('音频') !== -1) {
                                mediaFormView.compressing = false;
                                var videoHtml = '<div class="audio-box"></div>';
                                document.getElementById('mediaDiv').innerHTML = videoHtml;
                                Ext.Ajax.request({
                                    params: {entryid: entryid},
                                    url: '/electronic/getBrowseByEntryid',
                                    success: function (response) {
                                        var responseText = Ext.decode(response.responseText);
                                        if (responseText.data !== null) {
                                            var name = responseText.data.filename;
                                            name = name.substring(0, name.lastIndexOf('.'));
                                            var audioFn = audioPlay({
                                                song: [{
                                                    title: name,
                                                    src: responseText.data.filepath + "/" + responseText.data.filename,
                                                    cover: '../../img/defaultMedia/default_audio.png'
                                                }],
                                                error: function (msg) {
                                                    XD.msg(msg.meg);
                                                    console.log(msg)
                                                }
                                            });
                                            if (audioFn) {
                                                audioFn.loadFile(false);
                                            }
                                        }
                                    },
                                    failure: function () {
                                        XD.msg('获取浏览音频中断');
                                    }
                                });
                            }
                        },
                        failure: function () {
                            XD.msg('操作失败！');
                        }
                    });
                }
            }
        });
    },
    initFormField:function(form, operate, nodeid) {
        form.nodeid = nodeid;//用左侧树节点的id初始化form的nodeid参数
        form.removeAll();//移除form中的所有表单控件
        var field = {
            xtype: 'hidden',
            name: 'entryid'
        };
        form.add(field);
        var formField;
        if (dataSourceType == 'soundimage') {  //声像系统
            formField = form.getSxFormField();//根据节点id查询表单字段
            form.xtType = "声像系统";
        } else {
            formField = form.getFormField();//根据节点id查询表单字段
            form.xtType = "档案系统";
        }
        if (formField.length == 0) {
            XD.msg('请检查模板设置信息是否正确');
            return;
        }
        form.templates = formField;
        form.initField(formField,operate);//重新动态添加表单控件
        return '加载表单控件成功';
    },
    lookBack: function (btn) {
        btn.up('borrowFinishDetailView').setActiveItem(btn.up('borrowFinishDetailView').down('[itemId=gridview]'));
        if (window.play) {
            window.play(false);//音频停止播放
        }
        btn.up('mediaFormView').destroy();//销毁，防止视频在后台继续播放
    },
});

function getDateStr(AddDayCount) {
    var dd = new Date();
    dd.setDate(dd.getDate()+AddDayCount);//获取AddDayCount天后的日期
    var y = dd.getFullYear();
    var m = dd.getMonth()+1;//获取当前月份的日期
    var d = dd.getDate();
    if (m >= 1 && m <= 9) {
        m = "0" + m;
    }
    if (d >= 0 && d <= 9) {
        d = "0" + d;
    }
    return y+""+m+""+d;
}

