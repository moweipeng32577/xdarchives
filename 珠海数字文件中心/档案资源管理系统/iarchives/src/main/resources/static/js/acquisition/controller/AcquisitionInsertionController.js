/**
 * 数据采集-拆插件控制器
 * Created by Rong on 2018/6/20.
 */
Ext.define('Acquisition.controller.AcquisitionInsertionController', {
    extend: 'Ext.app.Controller',
    init:function() {
        this.control({
            'mediaItemsDataView [itemId=dismantle]':{//拆件
                click:this.dismantleHandler
            },
            'acquisitiongrid [itemId=dismantle]':{//拆件
                click:this.dismantleHandler
            },
            'entrygrid [itemId=innerfileDismantle]':{//卷内文件拆卷
                click:this.innerfileDismantleHandler
            },
            'acquisitionDismantleFormView [itemId=dismantleSave]':{//拆件（卷）－保存
                click:this.dismantleSubmitForm
            },
            'acquisitionDismantleFormView [itemId=dismantleBack]':{//拆件（卷）－返回
                click:function (btn) {
                    btn.up('window').close();
                }
            },
            'mediaItemsDataView [itemId=insertion]':{//插件
                click:this.insertionHandler
            },
            'acquisitiongrid [itemId=insertion]':{//插件
                click:this.insertionHandler
            },
            'entrygrid [itemId=innerfileInsertion]':{//卷内文件插卷
                click:this.innerfileInsertionHandler
            }
        });
    },

    /**
     * 获取数据采集主控制器
     * @returns {*|Ext.app.Controller}
     */
    findMainControl:function(){
        return this.application.getController('AcquisitionController');
    },


    //拆件
    dismantleHandler:function (btn) {
        var btnText=btn.getText();
        var grid = this.findMainControl().getGrid(btn);
        var record;
        var selectAll;
        if(grid.selModel != null) {
            record = grid.selModel.getSelection();
            selectAll = grid.down('[itemId=selectAll]').checked;
        }
        else {
            record = grid.acrossSelections;
        }
        var selectCount = record.length;

        if (selectAll) {
            selectCount = grid.selModel.selected.length;//当前页选中
        }
        var tree = this.findMainControl().findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if(!node){
            XD.msg('请选择节点');
            return;
        }
        if (selectCount == 0) {
            if (selectAll) {
                XD.msg('当前页没有选择数据');
            }else{
                XD.msg('请选择一条数据');
            }
            return;
        }
        if(selectCount != 1){
            if(btnText=='拆卷'){
                XD.msg('拆卷只能选中一条数据');
            }else{
                XD.msg('拆件只能选中一条数据');
            }
            return;
        }
        var entryid;
        if (selectAll) {
            entryid = grid.selModel.selected.items[0].get("entryid");
        } else {
            entryid = record[0].get("entryid");
        }
        var dismantleWin = Ext.create('Ext.window.Window',{
            width:'40%',
            height:300,
            title:btnText,
            draggable : true,//允许拖动
            resizable : false,//禁止缩放
            modal:true,
            closeToolText:'关闭',
            layout:'fit',
            items:[{
                xtype: 'acquisitionDismantleFormView',
                grid:grid,
                entryid:entryid
            }]
        });
        if(btnText=='拆卷'){
            dismantleWin.down('[itemId=dismantleType]').setFieldLabel('拆卷方式');
            dismantleWin.down('[itemId=dismantleNode]').setFieldLabel('拆卷到');
        }else{
            dismantleWin.down('[itemId=syncInnerFile]').hide();
        }
        dismantleWin.show();
    },

    //卷内文件拆件
    innerfileDismantleHandler:function (btn) {
        var grid = this.findMainControl().getInnerGrid(btn);
        var record = grid.selModel.getSelection();
        var selectCount = record.length;
        var tree = this.findMainControl().findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if(!node){
            XD.msg('请选择节点');
            return;
        }
        if(selectCount != 1){
            XD.msg('拆件只能选中一条数据');
            return;
        }
        var entryid;
        entryid = record[0].get("entryid");
        var dismantleWin = Ext.create('Ext.window.Window',{
            width:'40%',
            height:'40%',
            title:'卷内拆件',
            draggable : true,//允许拖动
            resizable : false,//禁止缩放
            modal:true,
            closeToolText:'关闭',
            layout:'fit',
            items:[{
                xtype: 'acquisitionDismantleFormView',
                grid:grid,
                entryid:entryid
            }]
        });
        dismantleWin.down('[itemId=syncInnerFile]').hide();
        dismantleWin.show();
    },

    //拆件（卷）　保存
    dismantleSubmitForm:function (btn) {
        var dismantleForm = btn.up('form');
        var dismantleWin = btn.up('window');
        var title=dismantleWin.title;//子件:拆件    母卷:拆卷
        if (title=='拆卷'){
            title='1';
        }else if(title=='卷内拆件'){
            title='2';
        }else if(title=='拆件'){
            title='3';
        }
        var dismantleType = dismantleForm.getForm().getValues()['dismantleType'];
        var syncType = dismantleForm.getForm().getValues()['syncInnerFile'];//判断是否同步卷内文件
        var acquisitionTreeComboboxView = dismantleForm.down('acquisitionTreeComboboxView');
        if(dismantleType==''){
            XD.msg('请选择拆件方式');
            return;
        }
        if(dismantleType=='node' && acquisitionTreeComboboxView.rawValue==''){
            XD.msg('请选择拆件目标节点');
            return;
        }
        if (dismantleType=='node' && dismantleForm.grid.nodefullname==acquisitionTreeComboboxView.rawValue) {
            XD.msg('不允许选择当前数据节点');
            return;
        }
        /* //拆件（卷）操作必须先处理后续数据，再拆除所选件（卷）
         var state = this.updateSubsequentData(dismantleForm.entryid,'dismantle');
         if(!state){
         return;
         }*/
        Ext.Ajax.request({
            url: '/acquisition/dismantle',
            async:false,
            params:{
                entryid:dismantleForm.entryid,
                dismantleType:dismantleType,
                nodeid:dismantleForm.nodeid,
                syncType:syncType,
                title:title
            },
            timeout:XD.timeout,
            success: function (resp) {
                var data = Ext.decode(resp.responseText);
                if(data.success){
                    dismantleForm.grid.notResetInitGrid();
                    //判断是否是上下层grid，如果是，则清空下层列表数据
                    if(dismantleForm.grid.itemId == 'northgrid'){
                        dismantleForm.grid.up('acquisitionFormAndGrid').down('[itemId=southgrid]').getStore().removeAll();
                    }
                }
                XD.msg(data.msg);
                dismantleWin.close();
            },
            failure:function () {
                XD.msg('操作失败');
            }
        });
    },

    //插件
    insertionHandler:function (btn) {
        var btnText=btn.getText();
        var grid = this.findMainControl().getGrid(btn);
        var acquisitionform = this.findMainControl().findFormToView(btn);
        acquisitionform.down('electronic').operateFlag='insertion';
        acquisitionform.operateFlag = 'insertion';
        var form = acquisitionform.down('dynamicform');
        var record;
        var selectCount;
        var selectAll;
        if(grid.selModel != null) {
            record = grid.selModel.getSelection();
            selectAll = grid.down('[itemId=selectAll]').checked;
        }
        else {
            record = grid.acrossSelections;
        }
        selectCount = record.length;
        if (selectAll) {
            selectCount = grid.selModel.selected.length;//当前页选中
        }
        var tree = this.findMainControl().findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if(!node){
            XD.msg('请选择节点');
            return;
        }
        if (selectCount == 0) {
            if (selectAll) {
                XD.msg('当前页没有选择数据');
            }else{
                XD.msg('请选择一条数据');
            }
            return;
        }
        if(selectCount != 1){
            if(btnText=='插卷'){
                XD.msg('插卷只能选中一条数据');
            }else{
                XD.msg('插件只能选中一条数据');
            }
            return;
        }
        var entryid;
        if (selectAll) {
            entryid = grid.selModel.selected.items[0].get("entryid");
        } else {
            entryid = record[0].get("entryid");
        }
        var initFormFieldState = this.findMainControl().initFormField(form, 'show', node.get('fnid'));
        if(!initFormFieldState){//表单控件加载失败
            return;
        }
        //form.operateFlag = 'insertion';//区别著录add和修改modify
        this.findMainControl().initFormData('insertion',form, entryid);
        this.findMainControl().activeToForm(form);
        this.findMainControl().loadFormRecord('insertion',form, entryid);//最后加载表单条目数据
        form.operateType = 'insertion';//点击保存按钮时判断是否更新后续统计项及档号值
        form.submitType='chji';//插件
        if(btnText=='插卷'){
            form.submitType='chju';//插卷
            XD.confirm('是否同步卷内文件',function(){
                form.submitType='syncJn';//插卷同步卷内
            });
        }
    },

    //卷内文件插卷
    innerfileInsertionHandler:function (btn) {
        var grid = this.findMainControl().getInnerGrid(btn);
        var acquisitionform = this.findMainControl().findFormToView(btn);
        acquisitionform.down('electronic').operateFlag='insertion';
        acquisitionform.operateFlag = 'insertion';
        var form = acquisitionform.down('dynamicform');
        var record = grid.selModel.getSelection();
        var selectCount = record.length;
        var tree = this.findMainControl().findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if(!node){
            XD.msg('请选择节点');
            return;
        }
        if(selectCount != 1){
            XD.msg('插件只能选中一条数据');
            return;
        }
        var entryid;
        entryid = record[0].get("entryid");
        var initFormFieldState = this.findMainControl().initFormField(form, 'hide', grid.dataParams.nodeid);
        if(!initFormFieldState){//表单控件加载失败
            return;
        }
        form.operateFlag = 'insertion';//区别著录add和修改modify
        this.findMainControl().initFormData('insertion',form, entryid);
        this.findMainControl().activeToForm(form);
        this.findMainControl().loadFormRecord('insertion',form, entryid);//最后加载表单条目数据
        //form.operateType = 'insertion';//点击保存按钮时判断是否更新后续统计项及档号值
        form.submitType='jnchj';//卷内插件
    }
});