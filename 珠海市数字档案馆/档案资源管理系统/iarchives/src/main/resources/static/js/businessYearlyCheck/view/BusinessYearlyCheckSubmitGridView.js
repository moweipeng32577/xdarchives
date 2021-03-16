/**
 * Created by Administrator on 2020/10/15.
 */


Ext.define('BusinessYearlyCheck.view.BusinessYearlyCheckSubmitGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'businessYearlyCheckSubmitGridView',
    itemId: 'businessYearlyCheckSubmitGridViewId',
    region: 'south',
    height: '60%',
    store: 'BusinessYearlyCheckSubmitGridStore',
    hasSearchBar: false,
    columns: [
        {text: '年度', dataIndex: 'selectyear', flex: 2, menuDisabled: true},
        {text: '题名', dataIndex: 'title', flex: 4, menuDisabled: true}
    ]
});
