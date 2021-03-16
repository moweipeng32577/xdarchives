/**
 * Created by tanly on 2017/12/1 0001.
 */
var store0 = Ext.create("Ext.data.Store", {
    fields: [],
    data: []
});

Ext.define('ReturnWare.view.NewInwareView', {
    //xtype: 'newInwareView',
    /*extend: 'Comps.view.EntryGridView',
    //dataUrl: '/inware/inware',
    tbar: [{
        text: '档案管理系统数据',
        itemId: 'show'
    }, '-', {
        text: '生成实体档案数据',
        itemId: 'add'
    }],
    searchstore:{
        proxy: {
            type: 'ajax',
            url:'/inware/inwareBy',//根据提名或档号查找对应实体档案
            extraParams:{nodeid:0},
            reader: {
                type: 'json',
                rootProperty: 'content',
                totalProperty: 'totalElements'
            }
        }
    }*/

    //extend:'Ext.window.Window',
    extend: 'Ext.panel.Panel',
    layout:'fit',
    xtype:'wareFormView',
    modal:true,
    items:[{
        xtype:'form',
        layout:'column',
        bodyPadding:10,
        defaults:{
            xtype:'textfield',
            labelAlign:'right',
            //labelStyle:'margin-top:20px',
            labelWidth:100,
            //margin:'5 5 0 5'
        },
        items:[{
            columnWidth:1,
            /*xtype:'textfield',
             fieldLabel: '存放位置',
             name:'description'*/
            layout : "column",
            xtype:'fieldset',
            style:'background:#fff;padding-top:0px',
            title: '存放位置',
            autoHeight:true,
            labelWidth:60,
            labelAlign:'right',
            animCollapse :true,

            autoScroll: true,
            renderTo: document.body,
            items:[
                {
                    columnWidth:.12,
                    xtype:"combo",
                    //name:'city',
                    id : 'city',
                    displayField:'citydisplay',
                    valueField:'citydisplay',
                    //store:store0,
                    store:'CityStore',
                    triggerAction:'all',
                    //queryMode: 'local',
                    forceSelection: false,
                    allowBlank:false,
                    editable: true,
                    emptyText:'选择城区',
                    blankText : '选择城区',
                    //value:'珠海',
                    listeners:{
                        select:function(combo, record,index){
                            try{
                                /* var parent0=this.up('wareFormView');
                                 var parent=parent0.down("shel");*/
                                var parent = Ext.getCmp("unit");
                                var parent1 = Ext.getCmp("room");
                                var parent2 = Ext.getCmp("zone");
                                var parent3 = Ext.getCmp("col");
                                var parent4=Ext.getCmp('section');
                                var parent5=Ext.getCmp('layer');
                                var parent6 = Ext.getCmp("side");
                                parent.clearValue();
                                parent1.clearValue();
                                parent2.clearValue();
                                parent3.clearValue();
                                parent4.clearValue();
                                parent5.clearValue();
                                parent6.clearValue();
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
                },{
                    xtype: 'displayfield',//空白间隔
                    id: 'picker8',
                    width: 5,
                },{
                    columnWidth:.15,
                    xtype:"combo",
                    //name:'unit',
                    id : 'unit',
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
                                var parent3=Ext.getCmp('section');
                                var parent4=Ext.getCmp('layer');
                                var parent5 = Ext.getCmp("side");
                                parent.clearValue();
                                parent1.clearValue();
                                parent2.clearValue();
                                parent3.clearValue();
                                parent4.clearValue();
                                parent5.clearValue();
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
                },{
                    xtype: 'displayfield',//空白间隔
                    id: 'picker7',
                    width: 5,
                },{
                    columnWidth:.17,
                    xtype:"combo",
                    //name:'room',
                    id : 'room',
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
                                var parent2=Ext.getCmp('section');
                                var parent3=Ext.getCmp('layer');
                                var parent4 = Ext.getCmp("side");
                                parent.clearValue();
                                parent1.clearValue();
                                parent2.clearValue();
                                parent3.clearValue();
                                parent4.clearValue();
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
                },{
                    xtype: 'displayfield',//空白间隔
                    id: 'picker1',
                    width: 5,
                    //height: 37
                },{
                    columnWidth:.19,
                    xtype:"combo",
                    //name:'zone',
                    id : 'zone',
                    displayField:'zonedisplay',
                    valueField:'zoneid',
                    store:'ZoneStore',
                    triggerAction:'all',
                    queryMode: 'local',
                    selectOnFocus:true,
                    forceSelection: true,
                    allowBlank:false,
                    editable: true,
                    emptyText:'选择密集架',
                    blankText : '选择密集架',
                    listeners:{
                        select:function(combo, record,index){
                            try{
                                var parent = Ext.getCmp("col");
                                var parent1=Ext.getCmp('section');
                                var parent2=Ext.getCmp('layer');
                                var parent3 = Ext.getCmp("side");
                                parent.clearValue();
                                parent1.clearValue();
                                parent2.clearValue();
                                parent3.clearValue();
                                parent.store.load({
                                    params:{
                                        zoneid:this.value
                                    }
                                });
                            }
                            catch(ex){
                                Ext.MessageBox.alert("错误","数据加载失败。");
                            }
                        }
                    }
                },{
                    xtype: 'displayfield',//空白间隔
                    id: 'picker2',
                    width: 5,
                },{
                    columnWidth:.15,
                    xtype:"combo",
                    //name:'col',
                    id : 'col',
                    displayField:'coldisplay',
                    valueField:'coldisplay',
                    store:'ColStore',
                    triggerAction:'all',
                    queryMode: 'local',
                    selectOnFocus:true,
                    forceSelection: true,
                    allowBlank:false,
                    editable: true,
                    emptyText:'选择列',
                    blankText : '选择列',
                    listeners:{
                        select:function(combo, record,index){
                            try{
                                var zoneid=Ext.getCmp("zone").getValue();
                                //var col=Ext.getCmp("col").getValue();
                                var parent=Ext.getCmp('section');
                                var parent1=Ext.getCmp('layer');
                                var parent2 = Ext.getCmp("side");
                                parent.clearValue();
                                parent1.clearValue();
                                parent2.clearValue();
                                parent.store.load({
                                    params:{
                                        zoneid:zoneid,
                                        col:this.value
                                    }
                                });
                            }
                            catch(ex){
                                Ext.MessageBox.alert("错误","数据加载失败。");
                            }
                        }
                    }
                },{
                    xtype: 'displayfield',//空白间隔
                    id: 'picker3',
                    width: 5,
                    //height: 37
                },{
                    columnWidth:.15,
                    xtype:"combo",
                    //name:'section',
                    id : 'section',
                    //fieldLabel:'选择列',
                    displayField:'sectiondisplay',
                    valueField:'sectiondisplay',
                    store:'SectionStore',
                    triggerAction:'all',
                    queryMode: 'local',
                    selectOnFocus:true,
                    forceSelection: true,
                    allowBlank:false,
                    editable: true,
                    emptyText:'选择节',
                    blankText : '选择节',
                    listeners:{
                        select:function(combo, record,index){
                            try{
                                var zoneid=Ext.getCmp("zone").getValue();
                                var col=Ext.getCmp("col").getValue();
                                var parent=Ext.getCmp('layer');
                                var parent1 = Ext.getCmp("side");
                                parent.clearValue();
                                parent1.clearValue();
                                parent.store.load({
                                    params:{
                                        zoneid:zoneid,
                                        col:col,
                                        section:this.value
                                    }
                                });
                            }
                            catch(ex){
                                Ext.MessageBox.alert("错误","数据加载失败。");
                            }
                        }
                    }
                },{
                    xtype: 'displayfield',//空白间隔
                    id: 'picker4',
                    width: 5,
                    //height: 37
                },{
                    columnWidth:.15,
                    xtype:"combo",
                    //name:'layer',
                    id : 'layer',
                    //fieldLabel:'选择列',
                    displayField:'layerdisplay',
                    valueField:'layerdisplay',
                    store:'LayerStore',
                    triggerAction:'all',
                    queryMode: 'local',
                    selectOnFocus:true,
                    forceSelection: true,
                    allowBlank:false,
                    editable: true,
                    emptyText:'选择层',
                    blankText : '选择层',
                    listeners:{
                        select:function(combo, record,index){
                            try{
                                var zoneid=Ext.getCmp("zone").getValue();
                                var col=Ext.getCmp("col").getValue();
                                var section=Ext.getCmp("section").getValue();
                                var parent=Ext.getCmp('side');
                                parent.clearValue();
                                parent.store.load({
                                    params:{
                                        zoneid:zoneid,
                                        col:col,
                                        section:section,
                                        layer:this.value
                                    }
                                });
                            }
                            catch(ex){
                                Ext.MessageBox.alert("错误","数据加载失败。");
                            }
                        }
                    }
                },{
                    xtype: 'displayfield',//空白间隔
                    id: 'picker5',
                    width: 5,
                    //height: 37
                },{
                    columnWidth:.15,
                    xtype:"combo",
                    //name:'side',
                    id : 'side',
                    //fieldLabel:'选择列',//节层面
                    displayField:'sidedisplay',
                    valueField:'sidedisplay',
                    store:'SideStore',
                    triggerAction:'all',
                    queryMode: 'local',
                    selectOnFocus:true,
                    forceSelection: true,
                    allowBlank:false,
                    editable: true,
                    emptyText:'选择面',
                    blankText : '选择面',
                    listeners:{
                        select:function(combo, record,index){
                            try{
                                var zoneid=Ext.getCmp("zone").getValue();
                                var col=Ext.getCmp("col").getValue();
                                var section=Ext.getCmp("section").getValue();
                                var layer=Ext.getCmp("layer").getValue();
                                var shid;
                                Ext.Ajax.request({
                                    url: '/shelves/shid',
                                    async:false,
                                    params:{
                                        zoneid:zoneid,
                                        col:col,
                                        section:section,
                                        layer:layer,
                                        side:this.value
                                    },
                                    success: function (response) {
                                        shid = Ext.decode(response.responseText);
                                        Ext.getCmp("shid").setValue(shid);
                                    }
                                });
                            }
                            catch(ex){
                                Ext.MessageBox.alert("错误","数据加载失败。");
                            }
                        }
                    }
                },{
                    xtype: 'displayfield',//空白间隔
                    id: 'picker10',
                    width: 5,
                    //height: 37
                }
            ]
        },{
            columnWidth:1,
            /*fieldLabel: '电子档案关联',
             name:'description'*/

            layout : "column",
            xtype:'fieldset',
            style:'background:#fff;padding-top:0px',
            title: '电子档案关联',
            autoHeight:true,
            labelWidth:60,
            labelAlign:'right',
            animCollapse :true,

            autoScroll: true,
            renderTo: document.body,
            items:[
                {
                    columnWidth:.18,
                    xtype: "button",
                    itemId: "dzbtn",
                    text: "选择档案"
                },{
                    //columnWidth:1,
                    xtype: 'displayfield',//空白间隔
                    id: 'picker6',
                    width: 5
                    //height: .1
                },{
                    columnWidth:1,
                    xtype:'textarea',
                    itemId: "idsName",
                    //fieldLabel: '电子档案',
                    //name:'idsName',
                    editable:false
                }
            ]

        },{
            columnWidth:1,
            xtype:'textfield',
            fieldLabel: '入库类型',
            value:'新增入库',
            name:'waretype',
            hidden: true
        },{
            xtype: 'displayfield',//空白间隔
            id: 'picker15',
            width: 5,
            height: 37
        },{
            columnWidth:1,
            xtype:'textarea',
            fieldLabel: '备注',
            name:'description'
        },{
            columnWidth:1,
            xtype:'textfield',
            itemId: "ids",
            fieldLabel: '电子档案编号',
            name:'ids',
            //name:'wareuser',
            hidden: true
        },{
            columnWidth:.3,
            xtype:'textfield',
            itemId: "shid",
            //fieldLabel: '位置编号',
            id:'shid',
            name:'shid',
            //name:'warenum',
            hidden: true
        }]
    }],
    buttons:[{text:'保存',itemId:'save'},{text:'返回',itemId:'cancel'}]
});