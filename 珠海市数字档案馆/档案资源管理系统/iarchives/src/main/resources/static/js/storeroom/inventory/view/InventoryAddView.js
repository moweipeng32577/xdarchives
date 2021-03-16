/**
 * Created by zengdw on 2018/05/09 0001.
 */

var store0 = Ext.create("Ext.data.Store", {
    fields: [],
    data: []
});

Ext.define('Inventory.view.InventoryAddView', {
    extend: 'Ext.panel.Panel',
    xtype: 'inventoryAddView',
    layout:'border',
    modal:true,
    items:[{
        xtype: 'form',
        itemId:'shelForm',
        region: 'north',
        layout: 'column',
        bodyPadding: 10,
        defaults: {
            xtype: 'textfield',
            labelAlign: 'right',
            labelWidth: 100,
            margin: '5 5 0 5'
        },
        items: [{
            columnWidth: 1,
            //region: 'north',
            layout: "column",
            xtype: 'fieldset',
            style: 'background:#fff;padding-top:0px',
            title: '选择盘点位置',
            autoHeight: true,
            labelWidth: 60,
            labelAlign: 'right',
            animCollapse: true,

            autoScroll: true,
            renderTo: document.body,
            items: [
                {
                    columnWidth: .12,
                    xtype: "combobox",
                    //name:'city',
                    id : 'city',
                    itemId: "city",
                    displayField:'citydisplay',
                    valueField:'citydisplay',
                    //queryMode: 'local',
                    store: {
                        type:'array',
                        fields:[{name:'citydisplay',mapping:function(data){return data}}],
                        proxy: {
                            type: 'ajax',
                            url: '/shelves/zones/distinct',
                        },
                        autoLoad: false
                    },
                    // margin:'5 10 0 10',
                    // triggerAction:'all',
                    // forceSelection: false,
                    //allowBlank:false,
                    editable: true,
                    emptyText:'选择城区',
                    blankText : '选择城区',
                    listeners:{
                        select:function(combo, record,index){
                            try{
                                /* var parent0=this.up('wareFormView');
                                 var parent=parent0.down("shel");*/
                                var parent = Ext.getCmp("unit");
                                var parent1 = Ext.getCmp("room");
                                var parent2 = Ext.getCmp("zone");
                                var parent3 = Ext.getCmp("col");

                                parent.clearValue();
                                parent1.clearValue();
                                parent2.clearValue();
                                parent3.clearValue();

                                parent.store.load({
                                    params:{
                                        citydisplay:this.value
                                    }
                                });
                            }
                            catch(ex){
                                Ext.MessageBox.alert("错误","数据加载失败。");
                            }
                        }
                    }
                }, {
                    xtype: 'displayfield',//空白间隔
                    id: 'picker8',
                    width: 5,
                }, {
                    columnWidth: .15,
                    xtype: "combo",
                    //name:'unit',
                    id : 'unit',
                    itemId: "unit",
                    displayField:'unitdisplay',
                    valueField:'unitdisplay',
                    //store:store0,
                    store:'UnitStore',
                    triggerAction:'all',
                    queryMode: 'local',
                    selectOnFocus:true,
                    forceSelection: false,
                    allowBlank:false,
                    editable: true,
                    emptyText:'选择单位',
                    blankText : '选择单位',
                    //value:'欣档',
                    listeners:{
                        select:function(combo, record,index){
                            try{
                                /* var parent0=this.up('wareFormView');
                                 var parent=parent0.down("shel");*/
                                var parent = Ext.getCmp("room");
                                var parent1 = Ext.getCmp("zone");
                                var parent2 = Ext.getCmp("col");

                                parent.clearValue();
                                parent1.clearValue();
                                parent2.clearValue();

                                var citydisplay=Ext.getCmp("city").getValue();
                                parent.store.load({
                                    params:{
                                        citydisplay:citydisplay,
                                        unitdisplay:this.value
                                    }
                                });
                            }
                            catch(ex){
                                Ext.MessageBox.alert("错误","数据加载失败。");
                            }
                        }
                    }
                }, {
                    xtype: 'displayfield',//空白间隔
                    id: 'picker7',
                    width: 5,
                }, {
                    columnWidth: .17,
                    xtype: "combo",
                    //name:'room',
                    id : 'room',
                    itemId: "room",
                    displayField:'roomdisplay',
                    valueField:'roomdisplay',
                    store:'RoomStore',
                    queryMode: 'local',
                    forceSelection: true,
                    allowBlank:false,
                    editable: false,
                    emptyText:'选择库房',
                    blankText : '选择库房',
                    listeners:{
                        select:function(combo, record,index){
                            try{
                                /* var parent0=this.up('wareFormView');
                                 var parent=parent0.down("shel");*/
                                var parent = Ext.getCmp("zone");
                                var parent1 = Ext.getCmp("col");

                                parent.clearValue();
                                parent1.clearValue();

                                var citydisplay=Ext.getCmp("city").getValue();
                                var unitdisplay=Ext.getCmp("unit").getValue();
                                parent.store.load({
                                    params:{
                                        citydisplay:citydisplay,
                                        unitdisplay:unitdisplay,
                                        roomdisplay:this.value
                                    }
                                });
                            }
                            catch(ex){
                                Ext.MessageBox.alert("错误","数据加载失败。");
                            }
                        }
                    }
                }, {
                    xtype: 'displayfield',//空白间隔
                    id: 'picker1',
                    width: 5,
                    //height: 37
                }, {
                    columnWidth: .19,
                    xtype: "combo",
                    //name:'zone',
                    id: 'zone',
                    itemId: "zone",
                    displayField: 'zonedisplay',
                    valueField: 'zoneid',
                    store: 'ZoneStore',
                    triggerAction: 'all',
                    queryMode: 'local',
                    selectOnFocus: true,
                    forceSelection: true,
                    allowBlank: false,
                    editable: true,
                    emptyText: '选择密集架',
                    blankText: '选择密集架',
                    listeners: {
                        select: function (combo, record, index) {
                            try {
                                var parent = Ext.getCmp("col");
                                parent.clearValue();
                                parent.store.load({
                                    params: {
                                        zoneid: this.value
                                    }
                                });
                            }
                            catch (ex) {
                                Ext.MessageBox.alert("错误", "数据加载失败。");
                            }
                        }
                    }
                }, {
                    xtype: 'displayfield',//空白间隔
                    id: 'picker2',
                    width: 5,
                }, {
                    columnWidth: .15,
                    xtype: "combo",
                    //name:'col',
                    id: 'col',
                    itemId: "col",
                    displayField: 'coldisplay',
                    valueField: 'coldisplay',
                    store: 'ColStore',
                    triggerAction: 'all',
                    queryMode: 'local',
                    selectOnFocus: true,
                    forceSelection: true,
                    allowBlank: false,
                    editable: true,
                    emptyText: '选择列',
                    blankText: '选择列',
                    listeners: {
                        select: function (combo, record, index) {
                            try {
                                var zoneid = Ext.getCmp("zone").getValue();
                                var rangeSto='col:'+zoneid+','+this.value;
                                Ext.getCmp("shid").setValue(rangeSto);
                            }
                            catch (ex) {
                                Ext.MessageBox.alert("错误", "数据加载失败。");
                            }
                        }
                    }
                }]
        }]
    },{
        xtype: 'form',
        itemId:'fileForm',
        region: 'center',
        layout: 'column',
        bodyPadding: 10,
        defaults: {
            xtype: 'textfield',
            labelAlign: 'right',
            labelWidth: 60,
            margin: '10 5 0 5'
        },
        items: [{
            columnWidth: .88,
            xtype: 'fileuploadfield',
            itemId: 'importFileNameID',
            // labelWidth: 80,
            fieldLabel: '盘点清单',
            buttonText: '浏览...',
            name: 'import'
        }, {
            xtype: 'label',
            columnWidth: .12,
            margin: '15 0 0 0',
            text: '温馨提示：请选择.txt格式的文件上传',
            style: {
                color: 'red',
                'font-size': '24px',
                'font-weight': 'bold'
            }
        }, {
            columnWidth: 1,
            xtype: 'textarea',
            fieldLabel: '备    注',
            name: 'description',
            itemId: 'description'
        }, {
            columnWidth: .3,
            xtype: 'textfield',
            itemId: "shid",
            //fieldLabel: '位置编号',
            id: 'shid',
            name: 'shid',
            itemId: 'shid',
            hidden: true
        }, {
            columnWidth: .3,
            xtype: 'textfield',
            itemId: "shidMsg",
            //fieldLabel: '位置详细',
            id: 'shidMsg',
            name: 'shidMsg',
            itemId: 'shidMsg',
            hidden: true
        }],
        //}]
    }],
    buttons: [{text: '盘点比对', itemId: 'save'}/*,{text:'取消',itemId:'cancel'}*/]

});