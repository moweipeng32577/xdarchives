/**
 * Created by RonJiang on 2017/11/2 0002.
 */
Ext.define('OriginalSearch.controller.OriginalSearchController',{
    extend : 'Ext.app.Controller',
    views :  ['OriginalSearchView','OriginalSearchGridView','OriginalSearchExportWin'],
    stores:  ['OriginalSearchGridStore'],
    models:  ['OriginalSearchGridModel'],
    init : function() {
        var originalSearchGridView;var tempParams=[];
        this.control({
            'originalSearchView [itemId=originalSearchSearchfieldId]':{
                search:function (searchfield) {
                    var originalSearchSearchView = searchfield.findParentByType('panel');
                    var condition = originalSearchSearchView.down('[itemId=originalSearchSearchComboId]').getValue(); //字段
                    var operator = 'like';//操作符
                    var content = searchfield.getValue(); //内容
                    //检索数据
                    //如果有勾选在结果中检索，则添加检索条件，如果没有勾选，则重置检索条件
                    var grid = originalSearchSearchView.findParentByType('panel').down('originalSearchGridView');
                    var gridstore = grid.getStore();
                    //加载列表数据
                    var searchcondition = condition;
                    var searchoperator = operator;
                    var searchcontent = content;
                    var inresult = originalSearchSearchView.down('[itemId=inresult]').getValue();
                    if(inresult){
                        var params = gridstore.getProxy().extraParams;
                        if(typeof(params.condition) != 'undefined'){
                            searchcondition = [params.condition,condition].join(XD.splitChar);
                            searchoperator = [params.operator,operator].join(XD.splitChar);
                            searchcontent = [params.content,content].join(XD.splitChar);
                        }
                    }

                    grid.dataParams={
                        condition: searchcondition,
                        operator: searchoperator,
                        content: searchcontent
                    };

                    //检索数据前,修改column的renderer，将检索的内容进行标红

                    Ext.Array.each(grid.getColumns(), function(){
                        var column = this;
                        if(!inresult && column.xtype == 'gridcolumn'){
                            column.renderer = function(value){
                                return value;
                            }
                        }
                        if(column.dataIndex == condition){
                            var searchstrs = [];
                            var conditions = searchcondition.split(XD.splitChar);
                            var contents = searchcontent.split(XD.splitChar);
                            for(var i =0;i<conditions.length;i++){
                                if(conditions[i] == condition){
                                    searchstrs.push(contents[i]);
                                }
                            }
                            column.renderer = function(value){
                                var contentData = searchstrs.join('|').split(' ');//切割以空格分隔的多个关键词
                                var reg = new RegExp(contentData.join('|'),'g');
                                return value.replace(reg,function (match) {
                                    return '<span style="color:red">'+match+'</span>';
                                });
                            }
                        }
                    });
                    grid.initGrid();
                }
            },
            'originalSearchGridView [itemId=exportId]':{
                click: function (view) {
                    var names ="";
                    var keys ="";
                    originalSearchGridView = view.findParentByType('originalSearchGridView');
                    var select = originalSearchGridView.getSelectionModel();
                    if (!select.hasSelection()) {
                        XD.msg('请选择需要导出的数据');
                        return;
                    } else {
                        var columnslist = originalSearchGridView.getColumns();
                        for (var i = 2; i < columnslist.length; i++) {
                            if (columnslist[i].dataIndex != null&&columnslist[i].dataIndex != 'null') {
                                names += columnslist[i].text + ",";
                                keys += columnslist[i].dataIndex + ",";
                            }
                        }
                        names+="档号,文件大小,文件页数,条目编号";
                        keys+="archivecode,filesize,pages,entryid";
                        Ext.create("OriginalSearch.view.OriginalSearchExportWin", {
                            names: names,
                            keys: keys,
                        }).show();
                    }
                }
            },
            'originalSearchExportWin [itemId=simpleSearchExportBtnId]':{
                click: function (view) {
                    var simpleSearchExportWin = view.findParentByType('originalSearchExportWin');
                    var names = simpleSearchExportWin.names;
                    var keys = simpleSearchExportWin.keys;
                    var exportFileName = simpleSearchExportWin.down('[itemId=simpleSearchExportFileNameId]').getValue();
                    var select;
                    var exportDetails
                    select = originalSearchGridView.getSelectionModel();
                    exportDetails = select.getSelection();
                    if(''==exportFileName){
                        XD.msg('文件名称不能为空');
                        return;
                    }
                    var pattern = new RegExp("[/:*?\"<>|]");
                    if(pattern.test(exportFileName) || exportFileName.indexOf('\\') > -1) {
                        XD.msg("文件名称不能包含下列任何字符：\\/:*?\"<>|");
                        return;
                    }
                    var array = "";
                    for (var i = 0; i < exportDetails.length; i++) {
                        array+= exportDetails[i].get('eleid')+",";
                    }
                    tempParams = originalSearchGridView.getStore().proxy.extraParams;
                    tempParams['fileName'] = exportFileName;
                    tempParams['userFieldCode'] = keys;
                    tempParams['userFieldName'] = names;
                    tempParams['ids'] = array;
                    Ext.MessageBox.wait('正在处理请稍后...');
                    Ext.Ajax.request({
                        method: 'post',
                        url:'/export/originalExport',
                        timeout:XD.timeout,
                        scope: this,
                        async:true,
                        params: tempParams,
                        success:function(res){
                            var responseText=Ext.decode(res.responseText);
                            var obj = responseText.data;
                            if(!responseText.success){
                                XD.msg(responseText.msg);
                                return;
                            }
                            if(obj.fileSizeMsg=="NO"){
                                XD.msg('原文总大小超出限制，一次只支持导出10G内的原文！');
                                Ext.MessageBox.hide();
                                return;
                            }
                            if(obj.entrySizeMsg=="NO"){
                                if(tempParams.exportState=="XmlAndFile"||tempParams.exportState=="ExcelAndFile"){
                                    XD.msg('条目数超出限制，一次只支持导出10万含原文的条目！');
                                }
                                if(tempParams.exportState=="Excel"||tempParams.exportState=="Xml"){
                                    XD.msg('条目数超出限制，一次只支持导出50w的条目！');
                                }
                                Ext.MessageBox.hide();
                                return;
                            }
                            window.location.href="/export/downloadZipFile?fpath="+encodeURIComponent(obj.filePath)
                            Ext.MessageBox.hide();
                            XD.msg('文件生成成功，正在准备下载');
                            simpleSearchExportWin.close()
                        },
                        failure:function(){
                            Ext.MessageBox.hide();
                            XD.msg('文件生成失败');
                        }
                    });
                }
            },
            'originalSearchExportWin [itemId=simpleSearchExportCloseBtnId]':{
                click: function (view) {
                    view.up('originalSearchExportWin').close();
                }
            },
            'EntryFormView [itemId="back"]':{
                click:function(view){
                    window.originalSearchShowWins.close();
                }
            }
        });
    }
});