/**
 * Created by yl on 2018/3/23.
 */
Ext.define('Reservation.view.ShowroomLookView', {
    extend: 'Ext.window.Window',
    xtype: 'ShowroomLookView',
    frame: true,
    resizable: true,
    title:'查看展厅',
    flag:'',
    width: 650,
    minWidth: 650,
    minHeight: 450,
    height:'90%',
    modal:true,
    closeToolText:'关闭',
    requires: [
        'Ext.layout.container.Border'
    ],
    scrollable:true,
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    defaults: {
        margin: '10 0 0 0'
    },
    items: [
        {
            xtype:'label',
            itemId:'title',
            style:{
                'font-size': '20px',
                'text-align':'center',
                'font-weight':'bold'
            }
        },
        {
            xtype:'label',
            itemId:'date',
            style:{
                'font-size': '15px',
                'text-align':'center'
            },
            margin: '20 0 0'
        },
        {
            width:650,
            height:390,
            html:'<iframe id="editFrame" src="htmledit"  width="100%" height="350px"' +
            ' style="margin:0px;border:0px;"></iframe>'
        }
    ]
});
