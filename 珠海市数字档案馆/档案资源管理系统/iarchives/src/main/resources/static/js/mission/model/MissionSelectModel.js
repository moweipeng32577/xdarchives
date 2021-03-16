/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Mission.model.MissionSelectModel', {
    extend: 'Ext.data.Model',
    xtype:'missionSelectModel',
    fields: [{name: 'id', type: 'string',mapping:'userid'},
        {name: 'realname', type: 'string'}]
});