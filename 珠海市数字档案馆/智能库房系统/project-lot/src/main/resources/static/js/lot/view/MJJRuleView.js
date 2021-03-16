/**
 * Created by wujy on 2019-09-05
 */
Ext.define('Lot.view.MJJRuleView',{
    extend:'Ext.grid.Panel',
    xtype:'MJJRule',
    itemId:'MJJRuleId',
    hasSearchBar:false,
    columns: [
        {text: 'A面', dataIndex: 'loginname', flex: 2, menuDisabled: true}
    ],
});