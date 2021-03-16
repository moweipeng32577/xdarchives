/**
 * 空调机组设备详细界面
 * Created by wujy on 2019-09-03.
 */
Ext.define('Lot.view.HT.HTConditionFormView',{
    extend:'Ext.form.FormPanel',
    xtype:'HTConditionForm',
    layout:'column',
    margin: '0 0 10 0',
    bodyPadding:5,
    items:[{
        columnWidth: .15,
        xtype: 'combo',
        itemId: 'floor',
        store: new Ext.data.Store({
            fields: ['floorName', 'floorid'],
            proxy: {
                type: 'ajax',
                url: '/floor/floors',
                reader: {
                    type: 'json',
                    rootProperty: 'content',
                    totalProperty: 'totalElements'
                }
            }
        }),
        valueField: 'floorid',
        displayField: 'floorName',
        queryMode:'all',
        name: 'floorName',
        fieldLabel: '楼层',
        labelWidth: 40,
        allowBlank: false,
        editable: true,
        margin:'0 25 0 0',
        listeners:{
            select:function (combo,value) {
                var form = combo.up('HTConditionForm');
                var roomCombo = form.down('[itemId=room]');
                var roomStore = roomCombo.getStore();
                roomStore.proxy.extraParams.floorid =value.data.floorid;
                roomStore.load();
            }
        }
    },{
        columnWidth: .15,
        xtype: 'combo',
        itemId: 'room',
        store: new Ext.data.Store({
            fields: ['id', 'name'],
            proxy: {
                type: 'ajax',
                url: '/deviceArea/getRooms',
                params:{
                  floorid:'',
                },
                reader: {
                    type: 'json',
                    rootProperty: 'content',
                    totalProperty: 'totalElements'
                }
            }
        }),
        valueField: 'id',
        displayField: 'name',
        queryMode:'all',
        name: 'name',
        fieldLabel: '库房',
        labelWidth: 40,
        allowBlank: false,
        editable: true,
        listeners:{
            expand:function (combo,value) {
                var form = combo.up('HTConditionForm');
                var floorCombo = form.down('[itemId=floor]');
                var floorValue = floorCombo.getValue();
                if(floorValue == null ){
                    XD.msg("请先选择楼层！")
                }
            }
        }
    },{
        text:'查询',
        xtype:'button',
        margin:'0 0 0 20',
        itemId:'search',
        width:60
    }]
});