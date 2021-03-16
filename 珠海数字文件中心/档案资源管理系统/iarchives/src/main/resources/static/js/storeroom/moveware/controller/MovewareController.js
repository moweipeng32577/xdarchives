/**
 * Created by Rong on 2018/4/27.
 */
Ext.define('Moveware.controller.MovewareController',{
    extend: 'Ext.app.Controller',

    views: ['MovewareView','MovewareTabView','ShelvesMoveView','EntryMoveShellView','ManagementView','ManagementGridView','InWareDetailView','EntityDetailView'],
    stores:['ShelvesGridStore','DetailGridStore','ManagementStore'],
    model:['ShelvesGridModel','DetailGridModel','ManagementModel'],

    init:function(){
        this.control({
            'shelvesMoveView [itemId=shelvesgrid]':{//点击选择库房区
                itemclick:this.itemClickHandler
            },
            'shelvesMoveView [itemId=detailgrid]':{//点击选择单元格
                cellclick:this.cellClickHandler
            },
            'shelvesMoveView [itemId=move]':{//点击移动
                click:this.moveHandler
            },
            'shelvesMoveView [itemId=add]':{//点击放置
                click:this.changeHandler
            },
            'shelvesMoveView [itemId=look]':{//点击查看
                click:this.showDetailHandler
            },
            'entryMoveShellView [itemId=look]':{//点击查看
                click:this.showEntryDetailHandler
            },
            /*'entryMoveShellView [itemId=dzbtnTwo]':{//点击选择电子档案
                click:this.changeHandler
             var appWindow = window.open("/QRcode/main?archivecode=" + archivecode);
            },*/
            'entryMoveShellView [itemId=shelvesgridTwo]':{//点击选择库房区
                itemclick:this.itemClickHandlerTwo
            },
            'entryMoveShellView [itemId=detailgridTwo]':{//点击选择单元格
                cellclick:this.cellClickHandlerTwo
            },
            'entryMoveShellView [itemId=dzbtn]':{//点击选择电子档案
                click:this.chooseHandler
            },
            'entryMoveShellView [itemId=addTwo]':{//点击放置電子檔案
                click:this.changeHandlerTwo
            },
            'managementView [itemId=treepanelId]':{
                select: function(treemodel, record){
                    var gridcard = treemodel.view.up('managementView').down('[itemId=gridcard]');
                    var onlygrid = gridcard.down('[itemId=onlygrid]');
                    var pairgrid = gridcard.down('[itemId=pairgrid]');
                    var grid;
                    var nodeType = record.data.nodeType;
                    var cls = record.data.cls;
                    var bgSelectOrgan =gridcard.down('[itemId=bgSelectOrgan]');
                    if(nodeType == 2){
                        gridcard.setActiveItem(bgSelectOrgan);
                        return;
                    }
                    gridcard.setActiveItem(onlygrid);
                    onlygrid.setTitle("当前位置：" + record.data.text);
                    grid = onlygrid;
                    var buttons = grid.down("toolbar").query('button');
                    var tbseparator = grid.down("toolbar").query('tbseparator');

                    grid.nodeid = record.get('fnid');
                    grid.initGrid({nodeid:record.get('fnid')});
                    var fullname=record.get('text');
                    while(record.parentNode.get('text')!='数据管理'){
                        fullname=record.parentNode.get('text')+'_'+fullname;
                        record=record.parentNode;
                    }
                    grid.nodefullname = fullname;
                    grid.parentXtype = 'management';
                    grid.formXtype = 'managementform';
                }
            },
            'managementgridView [itemId=idsBack]':{
                click:this.addIdsHandler
            },
            'managementgridView [itemId=idsClose]':{
                click:function(btn){
                    btn.findParentByType('managementView').close();
                }
            },
            'inWareDetailView [itemId=detailGridView]':{//点击打开档案详细
                itemclick:this.entryItemClickHandler
            }

        });
    },

    itemClickHandler:function(view, record, item){
        var zoneid = record.get('zoneid');
        var roomdisplay = record.get('roomdisplay');
        var zonedisplay = record.get('zonedisplay');

        //先清除移库页面标记
        var sourceText= view.findParentByType('shelvesMoveView').down('[itemId=sourceShid]');
        var targetText= view.findParentByType('shelvesMoveView').down('[itemId=targetShid]');
        var tempText= view.findParentByType('shelvesMoveView').down('[itemId=tempShid]');
        sourceText.setValue('');
        targetText.setValue('');
        tempText.setValue('');

        //记录zoneid,以便移库传参
        var zoneText= view.findParentByType('shelvesMoveView').down('[itemId=zoneId]');
        zoneText.setValue(zoneid);

        var detailgrid = view.findParentByType('shelvesMoveView').down('[itemId=detailgrid]');
        Ext.Ajax.request({
            url: '/shelves/zoneshel',
            async:false,
            params:{
                zoneid:zoneid
            },
            success: function (response) {
                var data = Ext.decode(response.responseText);
                var i=data.length;
                var colStr=data.substring(data.lastIndexOf(']')+1,data.length);
                var storeData=data.substring(0,data.lastIndexOf(']')+1);
                //重置该表格的model和store
                var tablejsondata= Ext.util.JSON.decode(storeData);//将json字符串转换为JSON对象数组
                var colArr=colStr.split(',');
                var fields = [];
                var columns = [];
                for(var i=0;i<colArr.length;i++) {
                    var colName = colArr[i];
                    columns.push({
                        text: colName,
                        dataIndex: colName,
                        menuDisabled:true,//取消箭头下拉排序
                        width:120,renderer: function(value, meta, record) {//设置自动换行
                            meta.style = 'align:center;overflow:visible;white-space:normal;';
                            return value;
                        }
                    });
                }
                fields.push(columns);
                Ext.create('Ext.data.Store', {
                    storeId:'ctlStore',
                    fields:fields,
                    data: tablejsondata,
                    proxy: {
                        type: 'memory',
                        reader: {
                            type: 'json'
                        }
                    }
                });
                detailgrid.reconfigure(Ext.data.StoreManager.lookup('ctlStore'),columns);

                var rowcount=0;
                detailgrid.store.each(function(r){//每一行
                    for(var i=0;i<colArr.length;i++) {//每一格
                        var cellStr=r.get(colArr[i]);
                        if(cellStr.indexOf('%')>0){
                            var num=cellStr.substring(cellStr.indexOf('层')+1,cellStr.indexOf('%')).trim();//截取百分比数字
                            if(Number(num)<100){
                                detailgrid.getView().getCell(rowcount,i).setStyle('background-color','#00EE00');
                            }else{
                                detailgrid.getView().getCell(rowcount,i).setStyle('background-color','#FF6347');
                            }
                        }
                    }
                    rowcount=rowcount+1;
                });

            },
            failure : function(form, action) {
                XD.msg('读取失败');
            }
        });

    },

    itemClickHandlerTwo:function(view, record, item){
        var zoneid = record.get('zoneid');
        var roomdisplay = record.get('roomdisplay');
        var zonedisplay = record.get('zonedisplay');

        //先清除移库页面标记
        var sourceText= view.findParentByType('entryMoveShellView').down('[itemId=sourceShidTwo]');
        var targetText= view.findParentByType('entryMoveShellView').down('[itemId=targetShidTwo]');
        var tempText= view.findParentByType('entryMoveShellView').down('[itemId=tempShidTwo]');
        sourceText.setValue('');
        targetText.setValue('');
        tempText.setValue('');

        //记录zoneid,以便移库传参
        var zoneText= view.findParentByType('entryMoveShellView').down('[itemId=zoneIdTwo]');
        zoneText.setValue(zoneid);

        var detailgrid = view.findParentByType('entryMoveShellView').down('[itemId=detailgridTwo]');
        Ext.Ajax.request({
            url: '/shelves/zoneshel',
            async:false,
            params:{
                zoneid:zoneid
            },
            success: function (response) {
                var data = Ext.decode(response.responseText);
                var i=data.length;
                var colStr=data.substring(data.lastIndexOf(']')+1,data.length);
                var storeData=data.substring(0,data.lastIndexOf(']')+1);
                //重置该表格的model和store
                var tablejsondata= Ext.util.JSON.decode(storeData);//将json字符串转换为JSON对象数组
                var colArr=colStr.split(',');
                var fields = [];
                var columns = [];
                for(var i=0;i<colArr.length;i++) {
                    var colName = colArr[i];
                    columns.push({
                        text: colName,
                        dataIndex: colName,
                        menuDisabled:true,//取消箭头下拉排序
                        width:120,renderer: function(value, meta, record) {//设置自动换行
                            meta.style = 'align:center;overflow:visible;white-space:normal;';
                            return value;
                        }
                    });
                }
                fields.push(columns);
                Ext.create('Ext.data.Store', {
                    storeId:'ctlStore',
                    fields:fields,
                    data: tablejsondata,
                    proxy: {
                        type: 'memory',
                        reader: {
                            type: 'json'
                        }
                    }
                });
                detailgrid.reconfigure(Ext.data.StoreManager.lookup('ctlStore'),columns);

                //单元格标注颜色
                var rowcount=0;
                detailgrid.store.each(function(r){//每一行
                    for(var i=0;i<colArr.length;i++) {//每一格
                        var cellStr=r.get(colArr[i]);
                        if(cellStr.indexOf('%')>0){
                            var num=cellStr.substring(cellStr.indexOf('层')+1,cellStr.indexOf('%')).trim();//截取百分比数字
                            if(Number(num)<100){
                                detailgrid.getView().getCell(rowcount,i).setStyle('background-color','#00EE00');
                            }else{
                                detailgrid.getView().getCell(rowcount,i).setStyle('background-color','#FF6347');
                            }
                        }
                    }
                    rowcount=rowcount+1;
                });
            },
            failure : function(form, action) {
                XD.msg('读取失败');
            }
        });

    },

    entryItemClickHandler:function(view, record, item){
        var archivecode = record.get('archivecode');
        /*var nw=window.open("/QRcode/main?archivecode=" + archivecode);
        nw.document.title = '库存档案详细信息.....';*/
        //window.open("/QRcode/main");

        var archivecodeArr = [];
        Ext.each(record,function (item) {
            if(""==item.data.archivecode||null==item.data.archivecode){
                archivecodeArr.push("id-"+item.data.entryid);
            }else {
                archivecodeArr.push(item.data.archivecode);
            }
        });
        archivecodeArr = archivecodeArr.join();

        var entityDetailView = new Ext.create('Moveware.view.EntityDetailView');
        entityDetailView .show();

        //档案详细页面
        var ran=Math.random();
        document.getElementById("frame1").src ="/QRcode/main?archivecode="+archivecodeArr+'&t='+ran;
    },

    cellClickHandler:function(table,td,cellIndex,record){//EXT GridPanel获取某一单元格的值
        var fileName;
        fileName=table.getHeaderAtIndex(cellIndex).dataIndex;//单元格的key
        var data = record.get(fileName);
        data=fileName+data;
        var chageCell = table.up('shelvesMoveView').down('[itemId=changeCell]');
        chageCell.setText(data);
        //XD.msg(data);
        //获取单元格信息后解析相应的单元格shid,放到一个位置来源，点击移动，清除位置目的存放的数据，
        // 再点击单元格，取单元格信息后解析相应的单元格shid,放到一个位置目的
        //点击放置，读取两个shid进行切换存储内容，然后刷新表格内容
        var sourceText=table.up('shelvesMoveView').down('[itemId=sourceShid]');
        var targetText=table.up('shelvesMoveView').down('[itemId=targetShid]');
        var tempText=table.up('shelvesMoveView').down('[itemId=tempShid]');
        var sourceStr=sourceText.getValue();
        var targetStr=targetText.getValue();
        var tempStr=tempText.getValue();
        if (sourceStr == '' || sourceStr == null || tempStr == '' || tempStr == null) {
            sourceText.setValue(data);
        }else{
            targetText.setValue(data);
        }

    },

    cellClickHandlerTwo:function(table,td,cellIndex,record){//EXT GridPanel获取某一单元格的值
        var fileName;
        fileName=table.getHeaderAtIndex(cellIndex).dataIndex;//单元格的key
        var data = record.get(fileName);
        data=fileName+data;
        //XD.msg(data);
        //获取单元格信息后解析相应的单元格shid,放到一个位置
        //点击放置，读取那个shid进行更新存储内容，然后刷新表格内容
        //var sourceText=table.up('entryMoveShellView').down('[itemId=sourceShidTwo]');
        var targetText=table.up('entryMoveShellView').down('[itemId=targetShidTwo]');
        var changeText=table.up('entryMoveShellView').down('[itemId=change]');
        //var tempText=table.up('entryMoveShellView').down('[itemId=tempShidTwo]');
       // var sourceStr=sourceText.getValue();
        var targetStr=targetText.getValue();
        //var tempStr=tempText.getValue();
        /*if (sourceStr == '' || sourceStr == null || tempStr == '' || tempStr == null) {
            sourceText.setValue(data);
        }else{*/
        changeText.setText(fileName);
        targetText.setValue(data);
       // }

    },

    moveHandler:function(btn){
        //先判断数据来源输入框是否为空，为空的话提示先指定一个单元格数据，不为空的话清空目标输入框,标记临时输入框tempShid
        var sourceText=btn.up('shelvesMoveView').down('[itemId=sourceShid]');
        // var MoveCell=btn.up('shelvesMoveView').down('[itemId=moveCell]');
        var str=sourceText.getValue();
        // MoveCell.setText(str);
        if (str == '' || str == null ) {XD.msg('请先选定一个需要移动的位置');return;}
        if (str.indexOf('%')<0 ) {XD.msg('选定的'+str+'没有需要移动的档案');return;}
        var targetText=btn.up('shelvesMoveView').down('[itemId=targetShid]');
        targetText.setValue('');
        var tempText=btn.up('shelvesMoveView').down('[itemId=tempShid]');
        tempText.setValue('1');
    },

    changeHandler:function(btn){
        var detailgrid =btn.up('shelvesMoveView').down('[itemId=detailgrid]');
        var sourceText=btn.up('shelvesMoveView').down('[itemId=sourceShid]');
        var targetText=btn.up('shelvesMoveView').down('[itemId=targetShid]');
        var tempText=btn.up('shelvesMoveView').down('[itemId=tempShid]');
        var zoneText= btn.up('shelvesMoveView').down('[itemId=zoneId]');
        // var putCell = btn.up('shelvesMoveView').down('[itemId=putCell]');
        var sourceStr=sourceText.getValue();
        var targetStr=targetText.getValue();
        // putCell.setText(targetStr);
        var zoneid=zoneText.getValue();
        //先判断数据不为空
        if (sourceStr == '' || sourceStr == null ) {XD.msg('请先选定一个需要移动的位置');return;}
        if (targetStr == '' || targetStr == null ) {XD.msg('请先选定一个需要放置的位置');return;}
        if(sourceStr==targetStr){
            XD.msg('移动位置不能和放置位置相同');
            return;
        }
        var msgStr='是否确定从'+sourceStr+'移库到'+targetStr;
        XD.confirm(msgStr,function(){
            Ext.Ajax.request({
                url: '/moveware/changeshel',
                async:false,
                params:{
                    sourceStr:sourceStr,
                    targetStr:targetStr,
                    zoneid:zoneid
                },
                success: function (resp) {
                    var respText = Ext.decode(resp.responseText);
                    if (respText.success == true) {
                        XD.msg(respText.msg);

                        //移库成功后清空来源和目的输入框
                        sourceText.setValue('');
                        targetText.setValue('');
                        tempText.setValue('');

                        //最后刷新页面
                        Ext.Ajax.request({
                            url: '/shelves/zoneshel',
                            async:false,
                            params:{
                                zoneid:zoneid
                            },
                            success: function (response) {
                                var data = Ext.decode(response.responseText);
                                var i=data.length;
                                var colStr=data.substring(data.lastIndexOf(']')+1,data.length);
                                var storeData=data.substring(0,data.lastIndexOf(']')+1);
                                //重置该表格的model和store
                                var tablejsondata= Ext.util.JSON.decode(storeData);//将json字符串转换为JSON对象数组
                                var colArr=colStr.split(',');
                                var fields = [];
                                var columns = [];
                                for(var i=0;i<colArr.length;i++) {
                                    var colName = colArr[i];
                                    columns.push({
                                        text: colName,
                                        dataIndex: colName,
                                        menuDisabled:true,//取消箭头下拉排序
                                        width:120,renderer: function(value, meta, record) {//设置自动换行
                                            meta.style = 'align:center;overflow:visible;white-space:normal;';
                                            return value;
                                        }
                                    });
                                }
                                fields.push(columns);
                                Ext.create('Ext.data.Store', {
                                    storeId:'ctlStore',
                                    fields:fields,
                                    data: tablejsondata,
                                    proxy: {
                                        type: 'memory',
                                        reader: {
                                            type: 'json'
                                        }
                                    }
                                });
                                detailgrid.reconfigure(Ext.data.StoreManager.lookup('ctlStore'),columns);

                                //单元格标注颜色
                                var rowcount=0;
                                detailgrid.store.each(function(r){//每一行
                                    for(var i=0;i<colArr.length;i++) {//每一格
                                        var cellStr=r.get(colArr[i]);
                                        if(cellStr.indexOf('%')>0){
                                            var num=cellStr.substring(cellStr.indexOf('层')+1,cellStr.indexOf('%')).trim();//截取百分比数字
                                            if(Number(num)<100){
                                                detailgrid.getView().getCell(rowcount,i).setStyle('background-color','#00EE00');
                                            }else{
                                                detailgrid.getView().getCell(rowcount,i).setStyle('background-color','#FF6347');
                                            }
                                        }
                                    }
                                    rowcount=rowcount+1;
                                });

                            },
                            failure : function(form, action) {
                                XD.msg('读取失败');
                            }
                        });
                    }else{
                        XD.msg(respText.msg);
                    }
                },
                failure : function(form, action) {
                    XD.msg('放置发生异常');
                }

            });

        });
    },

    changeHandlerTwo:function(btn){
        var detailgrid =btn.up('entryMoveShellView').down('[itemId=detailgridTwo]');
        //var sourceText=btn.up('entryMoveShellView').down('[itemId=sourceShidTwo]');
        var targetText=btn.up('entryMoveShellView').down('[itemId=targetShidTwo]');
        //var tempText=btn.up('entryMoveShellView').down('[itemId=tempShidTwo]');
        var zoneText= btn.up('entryMoveShellView').down('[itemId=zoneIdTwo]');
        var entryText= btn.up('entryMoveShellView').down('[itemId=ids]');
        //var sourceStr=sourceText.getValue();
        var targetStr=targetText.getValue();
        var zoneid=zoneText.getValue();
        var ids=entryText.getValue();
        //先判断数据不为空
        //if (sourceStr == '' || sourceStr == null ) {XD.msg('请先选定一个需要移动的位置');return;}
        if (ids == '' || ids == null ) {XD.msg('请先选定电子档案');return;}
        if (targetStr == '' || targetStr == null ) {XD.msg('请先选定一个需要放置的位置');return;}
        var msgStr='是否确定移库到'+targetStr;
        XD.confirm(msgStr,function(){
            Ext.Ajax.request({
                url: '/moveware/entrychangeshel',
                async:false,
                params:{
                    //sourceStr:sourceStr,
                    entryids:ids,
                    targetStr:targetStr,
                    zoneid:zoneid
                },
                success: function (resp) {
                    var respText = Ext.decode(resp.responseText);
                    if (respText.success == true) {
                        XD.msg(respText.msg);

                        //移库成功后清空来源和目的输入框
                        //sourceText.setValue('');
                        targetText.setValue('');
                        //tempText.setValue('');

                        //最后刷新页面
                        Ext.Ajax.request({
                            url: '/shelves/zoneshel',
                            async:false,
                            params:{
                                zoneid:zoneid
                            },
                            success: function (response) {
                                //this.initCell(response,detailgrid);
                                var data = Ext.decode(response.responseText);
                                var i=data.length;
                                var colStr=data.substring(data.lastIndexOf(']')+1,data.length);
                                var storeData=data.substring(0,data.lastIndexOf(']')+1);
                                //重置该表格的model和store
                                var tablejsondata= Ext.util.JSON.decode(storeData);//将json字符串转换为JSON对象数组
                                var colArr=colStr.split(',');
                                var fields = [];
                                var columns = [];
                                for(var i=0;i<colArr.length;i++) {
                                    var colName = colArr[i];
                                    columns.push({
                                        text: colName,
                                        dataIndex: colName,
                                        menuDisabled:true,//取消箭头下拉排序
                                        width:120,renderer: function(value, meta, record) {//设置自动换行
                                            meta.style = 'align:center;overflow:visible;white-space:normal;';
                                            return value;
                                        }
                                    });
                                }
                                fields.push(columns);
                                Ext.create('Ext.data.Store', {
                                    storeId:'ctlStore',
                                    fields:fields,
                                    data: tablejsondata,
                                    proxy: {
                                        type: 'memory',
                                        reader: {
                                            type: 'json'
                                        }
                                    }
                                });
                                detailgrid.reconfigure(Ext.data.StoreManager.lookup('ctlStore'),columns);

                                //单元格标注颜色
                                var rowcount=0;
                                detailgrid.store.each(function(r){//每一行
                                    for(var i=0;i<colArr.length;i++) {//每一格
                                        var cellStr=r.get(colArr[i]);
                                        if(cellStr.indexOf('%')>0){
                                            var num=cellStr.substring(cellStr.indexOf('层')+1,cellStr.indexOf('%')).trim();//截取百分比数字
                                            if(Number(num)<100){
                                                detailgrid.getView().getCell(rowcount,i).setStyle('background-color','#00EE00');
                                            }else{
                                                detailgrid.getView().getCell(rowcount,i).setStyle('background-color','#FF6347');
                                            }
                                        }
                                    }
                                    rowcount=rowcount+1;
                                });

                            },
                            failure : function(form, action) {
                                XD.msg('读取失败');
                            }
                        });
                    }else{
                        XD.msg(respText.msg);
                    }
                },
                failure : function(form, action) {
                    XD.msg('放置发生异常');
                }

            });

        });
    },

    chooseHandler:function(btn){
        var entryMoveShellView = btn.up('entryMoveShellView');
        var textfil=entryMoveShellView.down('[itemId=ids]');
        var namefil=entryMoveShellView.down('[itemId=idsName]');
        var entryid;
        var entrynames;
        var win = this.getView('ManagementView').create({
            title:'选择档案',
            modal: true,
            //resizable: false,
            width:'100%',
            height:'100%',
            maximizable : true,
            itemId:'chooseEntryView',
            closeAction: 'hide',
            listeners: {
                close: function (_this) {
                    entryid=_this.down('[itemId=entriesId]').getValue();
                    entrynames=_this.down('[itemId=entriesName]').getValue();
                    textfil.setValue(entryid);
                    var temp=entrynames.split(';');
                    var nameStr='';
                    for (i=0;i<temp.length ;i++ ){
                        nameStr+=temp[i]+'\n'
                    }
                    namefil.setValue(nameStr);
                }
            }
        });
        //绑定子窗口到父窗口
        entryMoveShellView.chooseEntryView = win;
        var button=win.down('[itemId=basicgridCloseBtn]');
        button.hide();//隐藏【关闭】按钮
        win.show();
    },

    addIdsHandler:function(btn){

        var grid = this.findActiveGrid(btn);
        var record = grid.selModel.getSelection();
        var selectCount = record.length;
        //XD.msg(selectCount);
        var selectAll = btn.findParentByType('managementView').down('[itemId=selectAll]').checked;
        if (selectAll) {
            selectCount = grid.selModel.selected.length;//当前页选中
        }
        var tree = this.findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if(!node){
            XD.msg('请选择节点');
            return;
        }
        if (selectAll && selectCount == 0) {
            XD.msg('当前页没有选中数据');
            return;
        }
        if (selectCount == 0) {
            XD.msg('当前页没有选中数据');
            return;
        }
        var entryid;
        var entryNames;
        if (selectAll) {
            entryid = grid.selModel.selected.items[0].get("entryid");
            var shidText=this.getShidTxt(grid.selModel.selected.items[0].get("archivecode"));
            entryNames = grid.selModel.selected.items[0].get("title");
        } else {
            if(selectCount==1){
                entryid = record[0].get("entryid");
                var dhCode=record[0].get("archivecode");
                var shidText=this.getShidTxt(dhCode);
                entryNames = dhCode+' , '+shidText+' , '+record[0].get("title")+';';
            }else{
                for (var i = 0; i < record.length; i++) {
                    entryid=entryid+record[i].get("entryid")+',';
                    var shidText=this.getShidTxt(record[0].get("archivecode"));
                    entryNames=entryNames+record[i].get("archivecode")+' , '+shidText+' , '+record[i].get("title")+';';
                }
                entryid=entryid.substring(9,entryid.length-1);
                entryNames=entryNames.substring(9,entryNames.length-1);
            }
        }

        //把数据entryid 放到隐藏文件框
        btn.findParentByType('managementView').down('[itemId=entriesId]').setValue(entryid);
        btn.findParentByType('managementView').down('[itemId=entriesName]').setValue(entryNames);

        //关闭窗口
        btn.findParentByType('managementView').close();
    },

    showDetailHandler:function(btn){//EXT GridPanel获取某一单元格的值,然后获取相应的库存信息显示
        //先判断数据来源输入框是否为空，为空的话提示先指定一个单元格数据，不为空的话清空目标输入框,标记临时输入框tempShid
        var sourceText=btn.up('shelvesMoveView').down('[itemId=sourceShid]');
        var str=sourceText.getValue();
        if (str == '' || str == null ) {XD.msg('请先选定一个需要查看的位置');return;}
        if (str.indexOf('%')<0 ) {XD.msg('选定的'+str+'没有可以查看的档案');return;}
        var zoneText= btn.up('shelvesMoveView').down('[itemId=zoneId]');
        var zoneid=zoneText.getValue();
        //var msgStr='是否确定查看'+sourceText+'的库存档案信息';
        var shelvesMoveView = btn.up('shelvesMoveView');
        var inid = str.substring(0,str.indexOf('层')+1)+zoneid;
        var win = this.getView('InWareDetailView').create({
            title:str+'&nbsp&nbsp&nbsp&nbsp&nbsp库存档案信息&nbsp&nbsp&nbsp&nbsp&nbsp绿色代表已入库&nbsp&nbsp&nbsp&nbsp&nbsp奶黄代表已出库',
            modal: true,
            //resizable: false,
            width:'85%',
            height:'75%',
            itemId:'detailView',
            baseProperty: inid
        });
        shelvesMoveView.detailView = win;
        var button=win.down('[itemId=basicgridCloseBtn]');//关闭所有页面按钮
        button.hide();//隐藏【关闭】按钮
        win.show();
        var gridcard=win.down('[itemId=detailGridView]');
        gridcard.initGrid({nodeid:templateNodeid});
        var rowcount=0;
        gridcard.getStore().on('load',function(s,records){
            s.each(function(r){
                if(r.get('nodefullname')=='已入库'){
                    gridcard.getView().getRow(rowcount).style.backgroundColor='#9AFF9A';
                }else{
                    gridcard.getView().getRow(rowcount).style.backgroundColor='#FFFACD';
                }
                rowcount=rowcount+1;
            });
        });
    },

    showEntryDetailHandler:function(btn){//EXT GridPanel获取某一单元格的值,然后获取相应的库存信息显示
        //先判断数据来源输入框是否为空，为空的话提示先指定一个单元格数据，不为空的话清空目标输入框,标记临时输入框tempShid
        var sourceText=btn.up('entryMoveShellView').down('[itemId=targetShidTwo]');
        var str=sourceText.getValue();
        if (str == '' || str == null ) {XD.msg('请先选定一个需要查看的位置');return;}
        if (str.indexOf('%')<0 ) {XD.msg('选定的'+str+'没有可以查看的档案');return;}
        var zoneText= btn.up('entryMoveShellView').down('[itemId=zoneIdTwo]');
        var zoneid=zoneText.getValue();
        //var msgStr='是否确定查看'+sourceText+'的库存档案信息';
        var shelvesMoveView = btn.up('entryMoveShellView');
        var inid = str.substring(0,str.indexOf('层')+1)+zoneid;
        var win = this.getView('InWareDetailView').create({
            title:str+'&nbsp&nbsp&nbsp&nbsp&nbsp库存档案信息&nbsp&nbsp&nbsp&nbsp&nbsp绿色代表已入库&nbsp&nbsp&nbsp&nbsp&nbsp奶黄代表已出库',
            modal: true,
            //resizable: false,
            width:'85%',
            height:'75%',
            itemId:'detailView',
            baseProperty: inid
        });
        shelvesMoveView.detailView = win;
        var button=win.down('[itemId=basicgridCloseBtn]');//关闭所有页面按钮
        button.hide();//隐藏【关闭】按钮
        win.show();
        var gridcard=win.down('[itemId=detailGridView]');
        gridcard.initGrid({nodeid:templateNodeid});

        gridcard.getStore().on('load',function(s,records){
            var rowcount=0;
            s.each(function(r){
                if(r.get('nodefullname')=='已入库'){
                    gridcard.getView().getRow(rowcount).style.backgroundColor='#9AFF9A';
                }else{
                    gridcard.getView().getRow(rowcount).style.backgroundColor='#FFFACD';
                }
                rowcount=rowcount+1;
            });
        });
    },

    initCell:function(response,detailgrid){
        var data = Ext.decode(response.responseText);
        var i=data.length;
        var colStr=data.substring(data.lastIndexOf(']')+1,data.length);
        var storeData=data.substring(0,data.lastIndexOf(']')+1);
        //重置该表格的model和store
        var tablejsondata= Ext.util.JSON.decode(storeData);//将json字符串转换为JSON对象数组
        var colArr=colStr.split(',');
        var fields = [];
        var columns = [];
        for(var i=0;i<colArr.length;i++) {
            var colName = colArr[i];
            columns.push({
                text: colName,
                dataIndex: colName,
                menuDisabled:true,//取消箭头下拉排序
                width:120,renderer: function(value, meta, record) {//设置自动换行
                    meta.style = 'align:center;overflow:visible;white-space:normal;';
                    return value;
                }
            });
        }
        fields.push(columns);
        Ext.create('Ext.data.Store', {
            storeId:'ctlStore',
            fields:fields,
            data: tablejsondata,
            proxy: {
                type: 'memory',
                reader: {
                    type: 'json'
                }
            }
        });
        detailgrid.reconfigure(Ext.data.StoreManager.lookup('ctlStore'),columns);

        //单元格标注颜色
        var rowcount=0;
        detailgrid.store.each(function(r){//每一行
            for(var i=0;i<colArr.length;i++) {//每一格
                var cellStr=r.get(colArr[i]);
                if(cellStr.indexOf('%')>0){
                    var num=cellStr.substring(cellStr.indexOf('层')+1,cellStr.indexOf('%')).trim();//截取百分比数字
                    if(Number(num)<100){
                        detailgrid.getView().getCell(rowcount,i).setStyle('background-color','#00EE00');
                    }else{
                        detailgrid.getView().getCell(rowcount,i).setStyle('background-color','#FF6347');
                    }
                }
            }
            rowcount=rowcount+1;
        });
    },

    findActiveGrid:function(btn){
        var active = this.findView(btn).down('[itemId=gridcard]').getLayout().getActiveItem();
        if(active.getXType() == "managementgridView"){
            return active;
        }else if(active.getXType() == "panel"){
            return active.down('[itemId=northgrid]');
        }
    },

    //获取列表界面视图
    findGridView:function(btn){
        return this.findView(btn).getComponent('gridview');
    },

    //获取数据管理应用视图
    findView:function(btn){
        return btn.findParentByType('managementView');
    },

    getShidTxt:function(dhCode){
        var shidTxt='';//库存位置信息
        Ext.Ajax.request({
            params: {dhCode: dhCode},
            url: '/storage/shid',
            method: 'post',
            async: false,
            success: function (response) {
                var respText = Ext.JSON.decode(response.responseText);
                //获取返回数据后设置到文本框
                if (respText.success == true) {
                    shidTxt=respText.msg;
                }
            },
            failure: function () {
                XD.msg('位置获取中断');
            }
        });
        return shidTxt;
    }

})