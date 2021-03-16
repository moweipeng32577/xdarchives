/**
 * Created by Administrator on 2020/7/2.
 */

Ext.define('Template.view.MetadataGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'metadataGridView',
    itemId: 'metadataGridViewId',
    searchstore: [{item: "fieldcode", name: "字段编码"}, {item: "fieldname", name: "字段描述"}],
    store: 'MetadataGridStore',
    columns: [
        {text: '所属表', dataIndex: 'fieldtable', flex: 2, menuDisabled: false},
        {text: '字段编码', dataIndex: 'fieldcode', flex: 2, menuDisabled: true},
        {text: '字段描述', dataIndex: 'fieldname', flex: 2, menuDisabled: true},
        {
            xtype: 'gridcolumn',
            flex: 2,
            dataIndex: 'metadatatype',
            text: '元数据类型',
            align: 'center',
            renderer: function (value, metaData, record) {
                var id = metaData.record.id+'metadatatype';
                var itemId = metaData.record.id+'Id';
                var gridId = record.id;
                Ext.defer(function () {
                    var comboBox = Ext.create('Ext.form.ComboBox', {
                        itemId:itemId,
                        queryMode: 'local',
                        margin: '0 0 0 50',
                        valueField:'fnid',
                        displayField:'text',
                        gridId:gridId,
                        renderTo: id,
                        store: {
                            proxy: {
                                type: 'ajax',
                                url: '/metadataTemplate/getClassifyById',
                                extraParams:{node:'root'},
                                reader: {
                                    type: 'json'
                                }
                            }
                        },
                        listeners:{
                            select:function(combo,records){
                                var metadatafieldnameCob;
                                for(var i=0;i<window.comboBoxArrt.length;i++){
                                    if(combo.itemId==window.comboBoxArrt[i].itemId){
                                        metadatafieldnameCob = window.comboBoxArrt[i];
                                        break;
                                    }
                                }
                                metadatafieldnameCob.getStore().proxy.extraParams.metadataType = records.get('fnid');
                                metadatafieldnameCob.getStore().reload();
                            }
                        }
                    });
                    var store = comboBox.getStore();
                    store.load(function () {
                        if(value!=''&&value!=undefined){
                            var select;
                            for(var i=0;i<store.getCount();i++){
                                var records = store.getAt(i);
                                if(value==records.get('fnid')){
                                    select = records;
                                    break;
                                }
                            }
                            comboBox.select(select);
                        }
                    });
                    window.comboBoxArrtType.push(comboBox);
                }, 50);
                return Ext.String.format('<div id="{0}"></div>', id);
            }
        }, {
            xtype: 'gridcolumn',
            flex: 2,
            dataIndex: 'metadatafieldname',
            text: '元数据字段描述',
            align: 'center',
            renderer: function (value, metaData, record) {
                var id = metaData.record.id+'metadatafieldname';
                var itemId = metaData.record.id+'Id';
                Ext.defer(function () {
                    var comboBox = Ext.create('Ext.form.ComboBox', {
                        itemId:itemId,
                        queryMode: 'local',
                        margin: '0 0 0 50',
                        valueField:'templateid',
                        displayField:'fieldname',
                        renderTo: id,
                        store: {
                            proxy: {
                                type: 'ajax',
                                url: '/metadataTemplate/findByClassify',
                                extraParams:{metadataType:''},
                                reader: {
                                    type: 'json'
                                }
                            }
                        }
                    });
                    var store = comboBox.getStore();
                    store.proxy.extraParams.metadataType = record.get('metadatatype');
                    if(record.get('metadatatype')!=''&&record.get('metadatatype')!=undefined){
                        store.load(function () {
                            if(value!=''&&value!=undefined){
                                var select;
                                for(var i=0;i<store.getCount();i++){
                                    var records = store.getAt(i);
                                    if(value==records.get('fieldname')){
                                        select = records;
                                        break;
                                    }
                                }
                                comboBox.select(select);
                            }
                        });
                    }
                    window.comboBoxArrt.push(comboBox);
                }, 50);
                return Ext.String.format('<div id="{0}"></div>', id);
            }
        }
    ]
});