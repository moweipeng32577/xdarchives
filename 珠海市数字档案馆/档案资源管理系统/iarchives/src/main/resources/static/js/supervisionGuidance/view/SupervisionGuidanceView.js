/**
 * Created by Administrator on 2020/7/8.
 */


Ext.define('SupervisionGuidance.view.SupervisionGuidanceView', {
    extend: 'Ext.panel.Panel',
    xtype: 'supervisionGuidanceView',
    layout: 'border',
    bodyBorder: false,
    items: [{
        itemId: 'topSearchId',
        region: 'north',
        height: 40,
        layout: 'column',
        items: [{
            columnWidth: .15,
            xtype: 'displayfield'
        }, {
            columnWidth: .2,
            xtype: 'organTreeView',
            itemId:'selectOrganId',
            labelWidth: 40,
            url: '/nodesetting/getOrganByParentId',
            extraParams: {pcid: '0',type:'all'},
            fieldLabel: '机构',
            // readOnly:allOrgan,   //监督数据总览可编辑，监督指导数据不可编辑
            queryMode: "local",
            margin: '10 0 0 0',
            allowBlank: false,
            editable: false,
            listeners: {
                // afterrender: function(view) {
                //     if(allOrgan){
                //         view.setDefaultValue(organId,organName);
                //     }
                // }
            }
        },{
            columnWidth: .01,
            xtype: 'displayfield'
        },{
            columnWidth: .2,
            xtype: 'combo',
            itemId: 'selectYearId',
            store: 'SelectYearStore',
            queryMode:'local',
            name: 'selectYear',
            fieldLabel: '年度',
            labelWidth: 40,
            displayField: 'selectyear',
            valueField: 'selectyear',
            margin: '10 0 0 0',
            allowBlank: false,
            listeners: {
                beforerender: function (combo) {
                    var store = combo.getStore();
                    store.on("load",function () {
                        if(store.getCount()>0){
                            if(combo.selctValue){  //选中保存刷新的年度
                                for(var i=0;i<store.getCount();i++){
                                    var record = store.getAt(i);
                                    if(combo.selctValue==record.get('selectyear')){
                                        combo.select(record);
                                        combo.fireEvent("select",combo,record);
                                        break;
                                    }
                                }
                            }else{
                                combo.select(store.getAt(0));
                                combo.fireEvent("select",combo,store.getAt(0));
                            }
                        }
                    });
                },
                select:function (view,record) {
                    // var supervisionGuidanceView = view.findParentByType('supervisionGuidanceView');
                    // var textLabel = supervisionGuidanceView.down('[itemId=textId]');
                    // if(allOrgan){
                    //     var textStr = textLabel.text.substring(0,textLabel.text.lastIndexOf('：')+1);
                    //     textLabel.setText(textStr+record.get('selectyear'));
                    // }else{
                    //     var selectOrgan = supervisionGuidanceView.down('[itemId=selectOrganId]');
                    //     var text;
                    //     if(selectOrgan.submitText==undefined){
                    //         text = '';
                    //     }else{
                    //         text = selectOrgan.submitText;
                    //     }
                    //     textLabel.setText('当前位置在 机构：'+text+' 年度：'+record.get('selectyear'));
                    // }
                    // var supervisionGuidanceTabView = supervisionGuidanceView.down('supervisionGuidanceTabView');
                    // var activeTitle = supervisionGuidanceTabView.getActiveTab().title;
                    // if (activeTitle == '档案组织体系') {
                    //     supervisionGuidanceView.down('[itemId=managerNumId]').setValue('');
                    //     supervisionGuidanceView.down('[itemId=workerNumId]').setValue('');
                    // }
                }
            }
        },{
            columnWidth: .01,
            xtype: 'displayfield'
        },{
            columnWidth: .08,
            style:{
                'text-align':'right'
            },
            itemId:'topSearchBtn',
            xtype : 'button',
            margin: '10 0 0 0',
            text:'检索'
        },{
            columnWidth: .2,
            xtype: 'label',
            text:'当前位置在 机构： 年度：',
            margin: '15 0 0 5',
            style:{
                'color':'red'
            },
            itemId:'textId'
        },{
            columnWidth: .15,
            xtype: 'displayfield'
        }]
    }, {
        region: 'center',
        xtype: 'supervisionGuidanceCenterView'
    }]
});
