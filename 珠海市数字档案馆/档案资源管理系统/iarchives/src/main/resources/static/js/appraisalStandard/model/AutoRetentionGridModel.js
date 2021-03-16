/**
 * Created by Leo on 2019/5/7 0007.
 */
Ext.define('AppraisalStandard.model.AutoRetentionGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string',mapping:'id'},
        {name: 'word', type: 'string'},
        {name: 'retention', type: 'string'},
        {name: 'nums', type: 'string'},
        {name: 'modifydate', type: 'string'}
    ]
});