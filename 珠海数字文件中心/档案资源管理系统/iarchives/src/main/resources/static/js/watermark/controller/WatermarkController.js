/**
 * Created by tanly on 2017/11/1 0024.
 */
Ext.define('Watermark.controller.WatermarkController', {
    extend: 'Ext.app.Controller',

    views: ['WatermarkView', 'WatermarkTreeView', 'WatermarkGridView', 'WatermarkPromptView','WatermarkAddFormView','WatermarkMediaView'],
    stores: ['WatermarkTreeStore', 'WatermarkGridStore'],
    models: ['WatermarkTreeModel', 'WatermarkGridModel'],
    init: function () {
        ifShowRightPanel = false;
        window.gzObj = {};
        this.control({
            'WatermarkTreeView': {
                select: this.treeSelect
            },

            'WatermarkGridView [itemid=addWatermarkBtn]':{
                click:this.addWatermarkBtn
            },

            'WatermarkAddFormView [itemId=nextId]':{//增加下一步
                click:this.lsadddenext
            },

            'WatermarkAddFormView [itemId=afterId]':{//增加上一步
                click:this.lsadddeafter
            },

            'WatermarkAddFormView [itemId=finishId]':{//水印增加/修改保存
                click:this.lssave
            },

            'WatermarkAddFormView [itemId=previewId]':{//水印增加/修改保存
                click:this.preview
            },

            'WatermarkGridView [itemid=editWatermarkBtn]':{
                click:this.editWatermarkBtn
            },

            'WatermarkGridView [itemid=delWatermarkBtn]':{
                click:this.delWatermarkBtn
            },
        });
    },

    treeSelect:function (treemodel) {
        var watermarkView = treemodel.view.findParentByType('WatermarkView');
        var watermarkPromptView = watermarkView.down('[itemId=WatermarkPromptViewID]');
        if (!ifShowRightPanel) {
            watermarkPromptView.removeAll();
            watermarkPromptView.add({
                xtype: 'WatermarkGridView'
            });
            ifShowRightPanel = true;
        }

        var watermarkgrid = watermarkPromptView.down('[itemId=WatermarkGridViewID]');
        window.gzObj.watermarkgrid = watermarkgrid;
        window.gzObj.organid = treemodel.selected.items[0].get('fnid');
        watermarkgrid.initGrid({organid: treemodel.selected.items[0].get('fnid')});
    },

    addWatermarkBtn:function (view) {
        var lsaddview = Ext.create("Ext.window.Window",{
            xtype:'lsAddfromWin',
            modal:true,
            width:'100%',
            height:'100%',
            title:'增加',
            layout:'fit',
            closeToolText:'关闭',
            items:[
                {
                    layout:'border',
                    height:'100%',
                    items: [
                        {
                            region:'west',
                            width: '50%',
                            layout:'fit',
                            items:[
                                {
                                    xtype: 'WatermarkAddFormView',
                                    mediaUrl:'/electronic/watermarkMedia?watermarkpath=',
                                    uploadUrl:'/electronic/watermarkElectronics',
                                    selUploadMediaUrl:'/electronic/electronics/capture/',
                                    isDigital:false,
                                    isAdd:true,
                                    mediaType:{
                                        title: 'Images',
                                        extensions: 'jpg,jpeg,png',
                                        miniTypes: 'image/*'
                                    },//电子文件现在类型
                                    selTitle:'请选择水印图片',//选择按钮文本
                                },
                            ]
                        },{
                            collapsible: false,
                            region:'center',
                            layout:'fit',
                            items:[
                                {
                                    width:'100%',
                                    height:'100%',
                                    html:'<iframe id="previewFrame" style="border:0px;width:100%;height:100%;" src="/watermark/media?type=nowatermark&number='+Math.random()+'"></iframe>'
                                }
                            ]
                        }]
                }
            ]
        });

        lsaddview.show();
        window.lsaddview = lsaddview;
        Ext.on('resize',function(a,b){
            window.lsaddview.setPosition(0, 0);
            window.lsaddview.fitContainer();
        });
    },

    lsadddenext:function(view){
        var formView = view.up('WatermarkAddFormView');
        if(formView.down('form').getForm().isValid()){//验证表单是否填写规范
            formView.setActiveItem(formView.down('WatermarkMediaView'));
            formView.down('[itemId=afterId]').show();
            view.hide();
        }
    },

    lsadddeafter:function(view){
        var formView = view.up('WatermarkAddFormView');
        formView.setActiveItem(formView.getComponent('lsformitemid'));
        formView.down('[itemId=nextId]').show();
        view.hide();
    },

    lssave:function(view){
        var formView = view.up('WatermarkAddFormView')
            ,mediaView = formView.down('WatermarkMediaView')
            ,form = formView.down('form');

        if(!form.isValid()){
            return;
        }

        if(mediaView.isAdd&&mediaView.watermarkPath==''&&form.down('[itemId=ispicture]').lastValue.ispicture=='1'){
            XD.msg("未上传图片");
            return;
        }

        if(mediaView.watermarkPath!=''&&form.down('[itemId=ispicture]').lastValue.ispicture=='1'){
            form.down('[itemId=watermark_picture_path]').setValue(mediaView.watermarkPath);
        }

        form.submit({
            waitMsg : '正在提交数据请稍后...',// 提示信息
            url : '/watermark/saveWatermark',
            method : 'POST',
            params : {
                organid :window.gzObj.organid,
            },
            success : function(form, action) {
                var respText = Ext.util.JSON.decode(action.response.responseText);
                if (respText.success == true) {
                    window.lsaddview.close();
                    //列表刷新区域
                    window.gzObj.watermarkgrid.initGrid({organid: window.gzObj.organid});
                }
                XD.msg(respText.msg);
            },
            failure : function(form, action) {
                XD.msg('操作失败');
            }
        });

    },

    preview:function (view,isEdit) {
        var formView = view.up('WatermarkAddFormView')
            ,mediaView = formView.down('WatermarkMediaView')
            ,form = formView.down('form');

        if(!form.isValid()){
            return;
        }

        if(!(isEdit===true)&&mediaView.isAdd&&mediaView.watermarkPath==''&&form.down('[itemId=ispicture]').lastValue.ispicture=='1'){
            XD.msg("未上传图片");
            return;
        }

        if(mediaView.watermarkPath!=''&&form.down('[itemId=ispicture]').lastValue.ispicture=='1'){
            form.down('[itemId=watermark_picture_path]').setValue(mediaView.watermarkPath);
        }

        form.submit({
            waitMsg : '正在生成预览...',// 提示信息
            url : '/watermark/previewWatermark',
            method : 'POST',
            params : {
                organid :window.gzObj.organid,
            },
            success : function(form, action) {
                var respText = Ext.util.JSON.decode(action.response.responseText);
                if (respText.success == true) {
                    // var previewview = Ext.create("Ext.window.Window",{
                    //     title:'水印预览',
                    //     width:'100%',
                    //     height:'100%',
                    //     html:'<iframe style="border:0px;width:100%;height:100%;" src="/watermark/preview"></iframe>'
                    // });
                    // previewview.show();
                    document.getElementById('previewFrame').setAttribute('src','/watermark/media?type=haswatermark&number='+Math.random());
                }
            },
            failure : function(form, action) {
                XD.msg('预览失败');
            }
        });
    },

    editWatermarkBtn:function (view) {
        var that = this;
        var selectedItems = window.gzObj.watermarkgrid.getSelectionModel().getSelected().items;
        if(window.gzObj.watermarkgrid.getSelectionModel().getCount()!=1){
            XD.msg('请选择一条数据');
            return;
        }

        var lsaddview = Ext.create("Ext.window.Window",{
            modal:true,
            width:'100%',
            height:'100%',
            title:'修改',
            layout:'fit',
            closeToolText:'关闭',
            items:[
                {
                    layout:'border',
                    height:'100%',
                    items: [
                        {
                            region:'west',
                            width: '50%',
                            layout:'fit',
                            items:[
                                {
                                    xtype: 'WatermarkAddFormView',
                                    mediaUrl:selectedItems[0].data.watermark_picture_path,
                                    uploadUrl:'/electronic/watermarkElectronics',
                                    selUploadMediaUrl:'/electronic/electronics/capture/',//无用
                                    isDigital:false,
                                    isAdd:false,
                                    mediaType:{
                                        title: 'Images',
                                        extensions: 'jpg,jpeg,png',
                                        miniTypes: 'image/*'
                                    },//电子文件现在类型
                                    selTitle:'请选择水印图片',//选择按钮文本
                                },
                            ]
                        },{
                            collapsible: false,
                            region:'center',
                            layout:'fit',
                            items:[
                                {
                                    width:'100%',
                                    height:'100%',
                                    html:'<iframe id="previewFrame" style="border:0px;width:100%;height:100%;" src=""></iframe>'
                                }
                            ]
                        }]
                }
            ]
        });
        lsaddview.down('form').load({
            url: '/watermark/getWatermark?id='+selectedItems[0].data.id,
            success : function(form, action) {
                var data=action.result.data;
                var x=0,y = 0;
                if(data.coordinates){
                    var xy = data.coordinates.split(',');
                    x = xy[0];
                    y = xy[1];
                }
                lsaddview.down('[itemId=coordinates1]').setValue(x);
                lsaddview.down('[itemId=coordinates2]').setValue(y);
                var preViewBtn = lsaddview.down('[itemId=previewId]');
                that.preview(preViewBtn,true);
            },
            failure: function(form, action) {
                XD.msg('操作失败');
            }
        });;
        lsaddview.show();
        window.lsaddview = lsaddview;
        Ext.on('resize',function(a,b){
            window.lsaddview.setPosition(0, 0);
            window.lsaddview.fitContainer();
        });
    },

    delWatermarkBtn:function (view) {
        var selectedItems = window.gzObj.watermarkgrid.getSelectionModel().getSelected().items
            ,docids = []
            ,paths = [];
        if(window.gzObj.watermarkgrid.getSelectionModel().getCount()<1){
            XD.msg('请至少选择一条数据');
            return;
        }
        for(var i=0;i<selectedItems.length;i++){docids.push(selectedItems[i].data.id);paths.push(selectedItems[i].data.watermark_picture_path);}

        XD.confirm('确定要删除这' + docids.length + '条数据吗',function(){
            Ext.Ajax.request({
                params: {ids: docids,paths:paths},
                url: '/watermark/delWatermarks',
                method: 'POST',
                sync: true,
                success: function (resp,opts) {
                    var respText = Ext.util.JSON.decode(resp.responseText);
                    XD.msg(respText.msg);
                    window.gzObj.watermarkgrid.initGrid({organid: window.gzObj.organid});
                }, failure: function(resp,opts) {
                    XD.msg('删除失败');
                }
            });
        },this);
    },
});