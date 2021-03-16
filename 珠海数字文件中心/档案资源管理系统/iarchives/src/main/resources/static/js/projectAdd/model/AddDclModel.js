/**
 * Created by Administrator on 2020/7/20.
 */
Ext.define('ProjectAdd.model.AddDclModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'title', type: 'string'},
        {name: 'workproject', type: 'string'},
        {name: 'workcontent', type: 'string'},
        {name: 'leaderrespon', type: 'string'},
        {name: 'undertakedepart', type: 'string'},
        {name: 'undertaker', type: 'string'},
        {name: 'cooperatedepart', type: 'string'},
        {name: 'finishtime', type: 'string'},
        {name: 'opinion', type: 'string'},
        {name: 'projectstatus', type: 'string'}
    ]
});