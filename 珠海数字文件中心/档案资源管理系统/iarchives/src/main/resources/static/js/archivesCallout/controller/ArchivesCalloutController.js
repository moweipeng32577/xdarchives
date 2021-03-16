var UnZipPath="";
Ext.define('ArchivesCallout.controller.ArchivesCalloutController', {
    extend: 'Ext.app.Controller',
    views: [
        'ArchivesCalloutView',
        'ArchivesCalloutGridView',
        'ArchivesCalloutAddBatchForm',
        'ArchivesCalloutAddEntryForm',
        'ArchivesCalloutAssemblyBatchForm',
        'ArchivesCalloutAssemblyBatchForm',
        'TreeComboboxView','ArchivesCalloutImportView'
    ],//加载view
    stores: [
        'ArchivesCalloutGridStore',
        'ArchivesCalloutEntryGridStore',
        'AssemblyStore'
    ],//加载store
    models: [
        'ArchivesCalloutGridModel',
        'ArchivesCalloutEntryGridModel'
    ],//加载model
    init: function () {
        this.control({
            'ArchivesCalloutView':{
                render:function(view){
                    var northGrid = view.down('ArchivesCalloutGridView');
                    var southGrid = view.down('[itemId=southgrid]');
                    window.viewGrid = {'northGrid':northGrid,'southGrid':southGrid}
                }
            },

            'ArchivesCalloutGridView':{
                itemclick:this.itemclickHandler
            },

            'ArchivesCalloutGridView [itemId=add]':{//调档
                click:this.batchcodeAdd
            },

            'ArchivesCalloutGridView [itemId=edit]':{//批次修改
                click:this.batchcodeEdit
            },

            'ArchivesCalloutGridView [itemId=del]':{//批次删除
                click:this.batchcodeDel
            },

            'ArchivesCalloutAddBatchForm [itemId=batchAddSubmit]':{//调档提交
                click:this.batchAddSubmit
            },

            'ArchivesCalloutAddBatchForm [itemId=batchAddClose]':{//调档返回
                click:this.batchAddClose
            },

            'ArchivesCalloutGridView [itemId=allot]':{//流水线分配
                click:this.batchAllot
            },

            'ArchivesCalloutAssemblyBatchForm [itemId=add]':{//流水线分配提交
                click:this.allotSubmit
            },

            'ArchivesCalloutAssemblyBatchForm [itemId=close]':{//流水线分配返回
                click:function (btn) {
                    btn.up('ArchivesCalloutAssemblyBatchForm').close();
                }
            },

            'ArchivesCalloutView [itemId=southgrid] [itemId=add]':{//批次条目增加
                click:this.entryAdd
            },

            'ArchivesCalloutView [itemId=southgrid] [itemId=edit]':{//批次条目修改
                click:this.entryEdit
            },

            'ArchivesCalloutView [itemId=southgrid] [itemId=returnback]':{//批次条目归还
                click:this.entryReturn
            },

            'ArchivesCalloutAddEntryForm [itemId=entryAddSubmit]':{
                click:this.entrySubmit
            },

            'ArchivesCalloutAddEntryForm [itemId=entryAddClose]':{
                click:this.entryClose
            },

            'ArchivesCalloutView [itemId=southgrid] [itemId=del]':{//批次条目删除
                click:this.entryDel
            },
            'ArchivesCalloutGridView [itemId=printIn]':{//进件登记单
                click:this.printReport
            }
            ,
            'ArchivesCalloutGridView [itemId=printOut]':{//换件登记单
                click:this.printReport
            },
            'ArchivesCalloutView [itemId=dowmLoad]': {
                click: function (view) {
                    var nodeid = "no";
                    var type = "yes";
                    var reqUrl="/archivesCallout/downloadEntryTemp?nodeid="+nodeid+"&&type="+type;
                    window.location.href=reqUrl;
                }
            },
            'ArchivesCalloutView [itemId=southgrid] [itemId=import]':{//批次条目导入条目
                click:function (view) {
                    var archivesCalloutGridView = view.findParentByType('ArchivesCalloutView').down('ArchivesCalloutGridView');
                    var calloutEntryGrid = view.findParentByType('ArchivesCalloutView').down('[itemId=southgrid]');
                    window.calloutEntryGrid = calloutEntryGrid;
                    var select = archivesCalloutGridView.getSelectionModel().getSelection();
                    var select2 = archivesCalloutGridView.getSelectionModel().getSelected();
                    var selectAll = archivesCalloutGridView.down('[itemId=selectAll]').checked;                    
                    if(select.length ==0 &&!selectAll){
                        XD.msg('请选择一个批次！');
                        return;
                    }
                    if(select.length >1 ){
                        XD.msg('只能选择一个批次');
                        return;
                    }
                    if(this.isOverCount(view)){
                        XD.msg('该批次档案份数已达上限,不能再添加条目');
                        return;
                    }
                    var batchcode = '';
                    var finishstate = '';
                    if(selectAll){
                        var d = archivesCalloutGridView.getSelectionModel().getSelected();
                        batchcode = d.items[0].get('batchcode');
                        finishstate = d.items[0].get('finishstate');
                    }else {
                        batchcode = select[0].get('batchcode');
                        finishstate = select[0].get('finishstate');
                    }
                    var isflag = this.isHasCalloutEntry(batchcode);
                    if(isflag){
                        //var finishstate = select[0].get('finishstate');
                        if(finishstate=='按时完成'||finishstate=='超时完成'){
                            XD.msg('当前批次已经完成，无法增加');
                            return;
                        }
                    }
                    //var batchid = select[0].get('batchcode');
                    var batchid = batchcode;
                    var entryImportView = Ext.create('Ext.window.Window',{
                        modal:true,
                        width:1100,
                        height:220,
                        layout:'fit',
                        title:'导入Excel',
                        closeToolText:'关闭',
                        closeAction:'hide',
                        items:[{
                            xtype: 'archivesCalloutImportView'
                        }]
                    });
                    entryImportView.down('archivesCalloutImportView').batchid = batchid;
                    var nodename = entryImportView.down('archivesCalloutImportView').down('TreeComboboxView');
                    nodename.setDefaultValue("4028801964ede9640164ee00d4000005","案卷管理_房产档案_全宗单位");
                    window.entryImportView = entryImportView;
                    entryImportView.show();
                }
            },
            'archivesCalloutImportView [itemId=import]': {
                click: this.impHandler
            },
            'archivesCalloutImportView [itemId=filefieldImport]': {
                change: this.fileSelected
            },
            'archivesCalloutImportView [itemId=dismantleNode]': {
                change: this.nodeSelected
            }
        });
    },

    /**
     * 选中目的节点
     * @param combo
     * @param item
     */
    nodeSelected: function (combo, item) {
        //如果源数据包和目的节点都有值，则提交表单
        var form = combo.up('form');
        var source = form.down('filefield');
        var nodeid = form.getForm().findField('nodename').getValue();
        var flag = this.getOrganid(nodeid);
        if (!flag) {
            XD.msg('当前选的节点不是机构节点');
            return;
        }
        if (source.getValue() != null) {
            this.submit(form);
        }
    },

    itemclickHandler:function(view, record, item, index, e){
        var batchcode =record.get('batchcode');
        var entryGridStore = viewGrid.southGrid.getStore();
        entryGridStore.proxy.extraParams.batchcode = batchcode;
        entryGridStore.reload();
        viewGrid.batchcode = batchcode;
    },

    batchcodeAdd:function(){
        this.showAddEditForm();
    },

    batchcodeEdit:function(btn){
        var select = viewGrid.northGrid.getSelectionModel();
        if (select.getSelected().length!=1) {
            XD.msg('至少选择一条数据');
            return;
        }
        var record = select.selected.items;
        this.showAddEditForm(record[0].get('id'));
    },

    batchcodeDel:function(btn){
        var select = viewGrid.northGrid.getSelectionModel();
        if (select.getSelected().length!=1) {
            XD.msg('至少选择一条数据');
            return;
        }
        var record = select.selected.items;
        var batchcodes = [];
        for (var i = 0; i < record.length; i++) {
            batchcodes.push(record[i].get('batchcode'));
        }

        XD.confirm('确定要删除选择的'+record.length+'条数据吗?',function() {
            Ext.Ajax.request({
                params: {'batchcodes': batchcodes},
                url: '/archivesCallout/batchDel',
                method: 'POST',
                sync: true,
                success: function (resp) {
                    var respText = Ext.decode(resp.responseText);
                    if (respText.success == true) {
                        viewGrid.northGrid.getStore().reload();
                        viewGrid.southGrid.getStore().reload();
                        XD.msg("删除成功");
                    } else {
                        XD.msg("删除失败");
                    }
                },
                failure: function () {
                    XD.msg('操作失败');
                }
            });
        },this);

    },

    showAddEditForm:function(id){
        var batchAddFormView = Ext.create('ArchivesCallout.view.ArchivesCalloutAddBatchForm');
        batchAddFormView.show();
        var batchAddForm = batchAddFormView.down('form');
        batchAddForm.load({
            url: '/archivesCallout/getBatchAddForm',
            params: {
                'id':id
            },
            success: function (form, action) {
            },
            failure: function () {
                XD.msg('获取表单信息失败');
            }
        });
    },


    batchAddSubmit:function(btn){
        var batchAddFormView = btn.up('ArchivesCalloutAddBatchForm');
        var batchAddForm = batchAddFormView.down('form');
        if(batchAddForm.isValid()){
            batchAddForm.submit({
                url: '/archivesCallout/batchAddFormSubmit',
                method: 'POST',
                success: function (response) {
                    XD.msg('操作成功');
                    batchAddFormView.close();
                    viewGrid.northGrid.getStore().reload();
                }, failure: function () {
                    XD.msg('操作失败');
                }
            });
        }
    },

    batchAddClose:function(btn){
        btn.up('ArchivesCalloutAddBatchForm').close();
    },

    batchAllot:function (btn) {
        var select = viewGrid.northGrid.getSelectionModel();
        if (select.getSelected().length!=1) {
            XD.msg('至少选择一条数据');
            return;
        }
        var record = select.selected.items;
        var batchcodes = [];
        for (var i = 0; i < record.length; i++) {
            batchcodes.push(record[i].get('batchcode'));
        }
        var allotView = Ext.create('ArchivesCallout.view.ArchivesCalloutAssemblyBatchForm');
        allotView.show();
    },

    allotSubmit:function(btn){
        var formView = btn.up('ArchivesCalloutAssemblyBatchForm');
        var assembly = $('input[name="assemblycode"]').val();
        var select = viewGrid.northGrid.getSelectionModel().getSelection();
        var ids = [];
        for (var i = 0; i < select.length; i++) {
            ids.push(select[i].get('id'));
        }
        //var id = select[0].get('id');
        var form = formView.down('form');
        var assemblycode = form.down('[name=assemblycode]').getValue();
        Ext.Ajax.request({
            url: '/archivesCallout/saveAssemblycode',
            method:'GET',
            params:{
                'ids': ids,
                'assemblycode': assemblycode,
                'assembly':assembly
            },
            success:function(response){
                XD.msg('操作成功');
                viewGrid.northGrid.getStore().reload();
            },failure:function(response,action){
                XD.msg('登记失败！');
            }
        });
        formView.close();
    },

    entryAdd:function (btn) {
        if(!viewGrid.batchcode){
            XD.msg('请先选择批次');
            return;
        }
        var entryAddForm = Ext.create('ArchivesCallout.view.ArchivesCalloutAddEntryForm');
        entryAddForm.show();
        var form = entryAddForm.down('form');
        // var nodename = form.down('[name=nodeid]');
        //  nodename.setDefaultValue("4028802d64ea40080164ea43283113de","案卷管理_文书档案_文书案卷_从化区国家档案馆");
        form.getForm().findField('batchcode').setValue(viewGrid.batchcode);
    },

    entryEdit:function (btn) {
        var select = viewGrid.southGrid.getSelectionModel();
        if (select.getSelected().length != 1) {
            XD.msg('请选择一条数据');
            return;
        }
        var record = select.selected.items;
        var entryAddForm = Ext.create('ArchivesCallout.view.ArchivesCalloutAddEntryForm');
        entryAddForm.show();
        var form = entryAddForm.down('form');
       form.load({
           url: '/archivesCallout/getEntryAddForm',
           params: {
               'id':record[0].get('id')
           },
           success: function (form, action) {
               var nodename = entryAddForm.down('[name=nodeid]');
               var nodeid = nodename.value;
               var nodefullname;
               Ext.Ajax.request({
                   method: 'post',
                   url: '/nodesetting/getNodefullnameLoop',
                   params: {
                       nodeid :nodeid
                   },
                   scope: this,
                   success: function (response) {
                       nodefullname = response.responseText;
                       nodename.setDefaultValue(nodeid,nodefullname.replace(/\"/g, ""));
                   }
               })
           },
           failure: function () {
               XD.msg('获取表单信息失败');
           }
       });
    },

    entryReturn:function (btn) {
        var select = viewGrid.southGrid.getSelectionModel().getSelection();
        if (select.length<1) {
            XD.msg('至少选择一条数据');
            return;
        }
        for(var i=0;i<select.length;i++){
            if(select[i].get('lendstate')=='已归还'){
                XD.msg('当前所选的批次条目中存在已归还条目');
                return;
            }
        }
        var entryids = [];
        for(var i=0;i<select.length;i++){
            entryids.push(select[i].get('id'));
        }
        XD.confirm("是否确定归还",function () {
        Ext.Ajax.request({
            url: '/archivesCallout/entryReturn',
            params: {
                entryids:entryids
            },
            success: function (form, action) {
                XD.msg('归还成功');
                viewGrid.southGrid.getStore().reload();
                viewGrid.northGrid.getStore().reload();
            },
            failure: function () {
                XD.msg('获取表单信息失败');
            }
        });
        });
    },

    entrySubmit:function (btn) {
        var formView = btn.up('ArchivesCalloutAddEntryForm');
        var form = formView.down('form');
        form.submit({
            url: '/archivesCallout/entryAddFormSubmit',
            method: 'POST',
            success: function (response) {
                XD.msg('操作成功');
                // formView.close();
                viewGrid.southGrid.getStore().reload();
            }, failure: function (response,action) {
                XD.msg(action.result.msg||'操作失败!');
            }
        });
    },

    entryClose:function (btn) {
        btn.up('ArchivesCalloutAddEntryForm').close();
    },


    entryDel:function (btn) {
        var select = viewGrid.southGrid.getSelectionModel();
        if (select.getSelected().length<1) {
            XD.msg('至少选择一条数据');
            return;
        }
        var record = select.selected.items;
        var ids = [];
        for (var i = 0; i < record.length; i++) {
            ids.push(record[i].get('id'));
        }
        var ArchivesCalloutGridView = btn.up('ArchivesCalloutView').down('ArchivesCalloutGridView');
        XD.confirm('确定要删除选择的'+record.length+'条数据吗?',function() {
            Ext.Ajax.request({
                params: {'ids': ids},
                url: '/archivesCallout/entryDel',
                method: 'POST',
                sync: true,
                success: function (resp) {
                    ArchivesCalloutGridView.getSelectionModel().clearSelections();
                    ArchivesCalloutGridView.getStore().reload();
                    var respText = Ext.decode(resp.responseText);
                    if (respText.success == true) {
                        viewGrid.southGrid.getStore().reload();
                        XD.msg("删除成功");
                    } else {
                        XD.msg("删除失败");
                    }
                },
                failure: function () {
                    XD.msg('操作失败');
                }
            });
        },this);
    },
    printReport:function(btn){
        var grid = btn.up('ArchivesCalloutGridView');
        var records = grid.getSelectionModel().getSelection();
        if(records.length!=1){
            XD.msg("只能选择一条数据");
            return;
        }
        var batchcode = records[0].get('batchcode');


        var url = '/ureport/preview?batchcode='+batchcode+'&_u=file:tj.ureport.xml&_t=1,4,5,6,7';
        var win = Ext.create('Ext.window.Window',{
            title:'统计报表',
            width:900,
            height:500,
            closeToolText:'关闭',
            html:'<iframe src="' + url + '" frameborder="0" style="width: 100%;height: 100%"></iframe>',
            buttons:[{
                text:'关闭',
                handler:function(){
                    win.close();
                }
            }]
        });
        win.show();
    },

    isOverCount:function(btn){
        var batchgrid = btn.up('ArchivesCalloutView').down('ArchivesCalloutGridView');
        var copies = batchgrid.getSelectionModel().getSelected().getAt(0).get('ajcopies');

        var entrygrid = btn.up('[itemId=southgrid]');
        var exists = entrygrid.getStore().getTotalCount();

        if(exists >= copies){
            return true;
        }
        return false;
    },
    isHasCalloutEntry: function (batchcode) {
        var isflag;
        Ext.Ajax.request({
            url: '/archivesCallout/isHasCalloutEntry',
            async: false,
            params: {
                batchcode:batchcode
            },
            success: function (response) {
                isflag = Ext.decode(response.responseText).data;
            }
        });
        return isflag;
    },

    /**
     * 选中源数据包（导入包含条目信息）
     * @param field
     * @param value
     */
    fileSelectedEntry: function (field, value) {
        //如果源数据包和目的节点都有值，则提交表单
        var form = field.up('form');
        var target = form.down('TreeComboboxView');
        var nodeid = form.getForm().findField('nodename').getValue();
        var flag = this.getOrganid(nodeid);
        if(!flag){
            XD.msg('当前选的节点不是机构节点');
            return;
        }
        if (target.getValue() != null) {
            this.submitEntry(form);
        }
    },
    /**
     * 选中源数据包
     * @param field
     * @param value
     */
    fileSelected: function (field, value) {
        //如果源数据包和目的节点都有值，则提交表单
        var form = field.up('form');
        var target = form.down('TreeComboboxView');
        var nodeid = form.getForm().findField('nodename').getValue();
        var flag = this.getOrganid(nodeid);
        if(!flag){
            XD.msg('当前选的节点不是机构节点');
            return;
        }
        if (target.getValue() != null) {
            this.submit(form);
        }
    },
    //判断节点是否为机构
    getOrganid:function (nodeid) {
        var organid;
        Ext.Ajax.request({
            url: '/import/getOgranid',
            async:false,
            params:{
                nodeid:nodeid
            },
            success: function (response) {
                organid = Ext.decode(response.responseText).data;
            }
        });
        return organid;
    },
    /**含条目信息
     * 完成源数据文件选择和目的数据节点选择
     * 解析文件格式
     * @param form
     */
    submit: function (form) {
        form.getForm().submit({
            url: '/import/upload',
            waitTitle: '提示',
            waitMsg: '请稍后，正在解析数据格式...',
            scope: this,
            params: {
                systype: 'Import'
            },
            success: function (basic, action) {
                //1.提交成功后，刷新字段设置
                var zipPath = action.result.UnZipPath;
                var filePath = action.result.fileTransferPath;
                if (zipPath != null) {
                    UnZipPath = zipPath;
                } else if (filePath != null) {
                    UnZipPath = filePath;
                }
            }
        });
    },
    /**
     * 开始执行导入
     * @param btn
     */
    impHandler: function (btn) {
        var workspace = btn.up('[itemId=workspace]');
        var importView = btn.findParentByType('archivesCalloutImportView');
        var basicform = workspace.down('form').getForm();
        var target = basicform.findField('nodename').getValue();
        var file = basicform.findField('source').getValue();
        var fileName = file.substring(file.lastIndexOf("\\") + 1);
        var myMask = new Ext.LoadMask({msg: '正在导入数据...', target: workspace});
        var fPath = UnZipPath;
        if(target==""||fileName==""){
            Ext.Msg.alert("提示","请确认节点数据和源文件是否正确！");
            return;
        }
        myMask.show();
        Ext.Ajax.setTimeout(3600000);
        Ext.Ajax.request({
            method: 'post',
            url: '/import/importCalloutEntry',
            params: {
                filePath:fPath,
                target: target,
                filename: fileName,
                batchid:importView.batchid
            },
            success: function (response, opts) {
                myMask.hide();
                if(response.responseText == ""){
                    Ext.MessageBox.alert('提示', '导入档案数量已超过该批次借出份数，无法导入！');
                    return;
                }
                var rep = Ext.decode(response.responseText);
                if(rep.erroMessage){
                    Ext.Msg.alert(rep.erroMessage);
                }else {
                    if (rep.error > 0) {
                        Ext.MessageBox.confirm('提示', '源数据文件共包含[' + rep.num + ']条数据，其中成功导入['
                            + (rep.num - rep.error) + ']条，失败[' + rep.error + ']条。点击确定后下载失败文件！',
                            function (btn, text) {
                                if (btn == 'yes') {
                                    var downForm = document.createElement('form');
                                    downForm.className = 'x-hidden';
                                    downForm.method = 'post';
                                    downForm.action = '/import/downloadImportFailure';
                                    var data = document.createElement('input');
                                    data.type = 'hidden';
                                    data.name = 'file';
                                    data.value = rep.errorfile;
                                    downForm.appendChild(data);
                                    document.body.appendChild(downForm);
                                    downForm.submit();
                                } else {
                                    Ext.Ajax.request({
                                        method: 'post',
                                        url: '/import/deleteFailureFile',
                                        params: {
                                            confirm: 'confirm'
                                        }
                                    });
                                }
                                window.calloutEntryGrid.getStore().reload();
                                window.entryImportView.close();
                            }, this);
                    } else {
                        Ext.MessageBox.alert('提示', '源数据文件共包含[' + rep.num + ']条数据，成功导入['
                            + (rep.num - rep.error) + ']条');
                        window.calloutEntryGrid.getStore().reload();
                        window.entryImportView.close();
                    }
                }
            },
            failure: function (response, opts) {
                myMask.hide();
                var rep = Ext.decode(response.responseText);
                Ext.Msg.alert(rep);
            }
        });

    }
});