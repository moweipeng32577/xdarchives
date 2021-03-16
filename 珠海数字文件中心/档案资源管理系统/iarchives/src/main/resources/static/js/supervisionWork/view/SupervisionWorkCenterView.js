/**
 * Created by Administrator on 2020/10/12.
 */

var receivetypeMode = Ext.create("Ext.data.Store",{
    fields:["text","value"],
    data:[
        {text:"文书",value:"文书"},
        {text:"照片",value:"照片"},
        {text:"录音录像",value:"录音录像"}
    ]
});


Ext.define('SupervisionWork.view.SupervisionWorkCenterView', {
    extend: 'Ext.panel.Panel',
    xtype: 'supervisionWorkCenterView',
    layout: 'fit',
    items: [{
        xtype: 'form',
        layout: 'column',
        itemId: 'fieldsetFormId',
        autoScroll: true,
        items: [{
            xtype: 'textfield',
            fieldLabel: '',
            name: 'id',
            hidden: true
        }, {
            columnWidth: 1,
            xtype: 'fieldset',
            title: "归档",
            margin: '0 10 0 10',
            layout: 'column',
            items: [{
                columnWidth: .3,
                xtype: 'checkbox',
                itemId: 'isunitmaterialId',
                name: 'isunitmaterial',
                inputValue: '1',
                boxLabel: '是否编制本单位的文件材料'
            },{
                columnWidth: .05,
                xtype: 'displayfield'
            },{
                columnWidth: .2,
                xtype: 'textfield',
                itemId:'fillingnameId',
                fieldLabel: '归档范围和档案保管期限表',
                labelWidth: 200,
                editable: false,
                name: 'fillingname'
            },{
                columnWidth: .06,
                style : 'text-align:center;',
                margin: '5 0 0 5',
                items: [
                    {
                        xtype: 'label',
                        itemId:'fillingnamecount',
                        text: '共0份'
                    }
                ]
            }, {
                columnWidth: .04,
                margin: '0 0 0 5',
                items: [
                    {
                        xtype: 'button',
                        itemId:'fillingnameUpId',
                        text: '上传'
                    }
                ]
            },{
                columnWidth: .05,
                xtype: 'displayfield'
            },{
                columnWidth: .3,
                xtype: 'checkbox',
                itemId: 'isontimeId',
                name: 'isontime',
                inputValue: '1',
                boxLabel: '各门类、载体的文件材料是否按规定及时归档且齐全完整'
            },{
                columnWidth: .3,
                xtype: 'textfield',
                itemId: 'roomdocfilenumId',
                fieldLabel: '（室藏）档案数[文书档案（件）]',
                margin: '5 0 0 0',
                name: 'roomdocfilenum',
                labelWidth: 200
            }, {
                columnWidth: .05,
                xtype: 'displayfield'
            },{
                columnWidth: .3,
                xtype: 'textfield',
                itemId: 'roomdocfilesnumId',
                fieldLabel: '（室藏）档案数[文书档案（卷）]',
                margin: '5 0 0 0',
                name: 'roomdocfilesnum',
                labelWidth: 200
            }, {
                columnWidth: .05,
                xtype: 'displayfield'
            },{
                columnWidth: .3,
                xtype: 'textfield',
                itemId: 'yeardocnumId',
                fieldLabel: '（本年度）增加数[文书档案]',
                margin: '5 0 0 0',
                name: 'yeardocnum',
                labelWidth: 200
            }, {
                columnWidth: .3,
                xtype: 'textfield',
                itemId: 'roombasefilesnumId',
                fieldLabel: '（室藏）档案数[基建档案（卷）]',
                margin: '5 0 0 0',
                name: 'roombasefilesnum',
                labelWidth: 200
            }, {
                columnWidth: .05,
                xtype: 'displayfield'
            }, {
                columnWidth: .3,
                xtype: 'textfield',
                itemId: 'yearbasenumId',
                fieldLabel: '（本年度）增加数[基建档案]',
                margin: '5 0 0 0',
                name: 'yearbasenum',
                labelWidth: 200
            }, {
                columnWidth: .05,
                xtype: 'displayfield'
            }, {
                columnWidth: .3,
                xtype: 'textfield',
                itemId: 'roomeqfilesnumId',
                fieldLabel: '（室藏）档案数[设备档案（卷）]',
                margin: '5 0 0 0',
                name: 'roomeqfilesnum',
                labelWidth: 200
            }, {
                columnWidth: .3,
                xtype: 'textfield',
                itemId: 'yeareqnumId',
                fieldLabel: '（本年度）增加数[设备档案]',
                margin: '5 0 0 0',
                name: 'yeareqnum',
                labelWidth: 200
            }, {
                columnWidth: .05,
                xtype: 'displayfield'
            }, {
                columnWidth: .3,
                xtype: 'textfield',
                itemId: 'roomaccountfilesnumId',
                fieldLabel: '（室藏）档案数[会计档案（卷）]',
                margin: '5 0 0 0',
                name: 'roomaccountfilesnum',
                labelWidth: 200
            }, {
                columnWidth: .05,
                xtype: 'displayfield'
            }, {
                columnWidth: .3,
                xtype: 'textfield',
                itemId: 'roomaccountcopiesnumId',
                fieldLabel: '（室藏）档案数[会计档案（册）]',
                margin: '5 0 0 0',
                name: 'roomaccountcopiesnum',
                labelWidth: 200
            }, {
                columnWidth: .3,
                xtype: 'textfield',
                itemId: 'yearaccountnumId',
                fieldLabel: '（本年度）增加数[会计档案]',
                margin: '5 0 0 0',
                name: 'yearaccountnum',
                labelWidth: 200
            }, {
                columnWidth: .05,
                xtype: 'displayfield'
            }, {
                columnWidth: .3,
                xtype: 'textfield',
                itemId: 'roomsxfilenumId',
                fieldLabel: '（室藏）档案数[声像档案（件）]',
                margin: '5 0 0 0',
                name: 'roomsxfilenum',
                labelWidth: 200
            }, {
                columnWidth: .05,
                xtype: 'displayfield'
            }, {
                columnWidth: .3,
                xtype: 'textfield',
                itemId: 'yearsxnumId',
                fieldLabel: '（本年度）增加数[声像档案]',
                margin: '5 0 0 0',
                name: 'yearsxnum',
                labelWidth: 200
            }, {
                columnWidth: .3,
                xtype: 'textfield',
                itemId: 'roomphysicalnumId',
                fieldLabel: '（室藏）档案数[实物档案（件）]',
                margin: '5 0 0 0',
                name: 'roomphysicalnum',
                labelWidth: 200
            }, {
                columnWidth: .05,
                xtype: 'displayfield'
            }, {
                columnWidth: .3,
                xtype: 'textfield',
                itemId: 'yearphysicalnumId',
                fieldLabel: '（本年度）增加数[实物档案]',
                margin: '5 0 0 0',
                name: 'yearphysicalnum',
                labelWidth: 200
            },{
                columnWidth: .05,
                xtype: 'displayfield'
            }, {
                columnWidth: .3,
                xtype: 'textfield',
                itemId: 'roomspecialfilenumId',
                fieldLabel: '（室藏）档案数[专门档案（件）]',
                margin: '5 0 0 0',
                name: 'roomspecialfilenum',
                labelWidth: 200
            }, {
                columnWidth: .3,
                xtype: 'textfield',
                itemId: 'roomspecialfilesnumId',
                fieldLabel: '（室藏）档案数[专门档案（卷）]',
                margin: '5 0 0 0',
                name: 'roomspecialfilesnum',
                labelWidth: 200
            }, {
                columnWidth: .05,
                xtype: 'displayfield'
            }, {
                columnWidth: .3,
                xtype: 'textfield',
                itemId: 'yearspecialnumId',
                fieldLabel: '（本年度）增加数[专门档案]',
                margin: '5 0 0 0',
                name: 'yearspecialnum',
                labelWidth: 200
            }]
        },{
            columnWidth: 1,
            xtype: 'fieldset',
            title: "整理",
            margin: '0 10 0 10',
            layout: 'column',
            items: [{
                columnWidth: .3,
                xtype: 'textfield',
                itemId:'classplannameId',
                fieldLabel: '档案分类标识方案',
                labelWidth: 200,
                editable: false,
                name: 'classplanname'
            },{
                columnWidth: .06,
                style : 'text-align:center;',
                margin: '5 0 0 5',
                items: [
                    {
                        xtype: 'label',
                        itemId:'classplannamecount',
                        text: '共0份'
                    }
                ]
            }, {
                columnWidth: .04,
                margin: '0 0 0 5',
                items: [
                    {
                        xtype: 'button',
                        itemId:'classplannameUpId',
                        text: '上传'
                    }
                ]
            },{
                columnWidth: .1,
                xtype: 'displayfield'
            },{
                columnWidth: .4,
                xtype: 'checkbox',
                itemId: 'isclearupId',
                name: 'isclearup',
                inputValue: '1',
                boxLabel: '各类档案是否全部整理上架'
            }]
        },{
            columnWidth: 1,
            xtype: 'fieldset',
            title: "保管",
            margin: '0 10 0 10',
            layout: 'column',
            items: [{
                columnWidth: .2,
                xtype: 'textfield',
                itemId:'fundsfilesId',
                fieldLabel: '建立全宗卷',
                labelWidth: 200,
                editable: false,
                name: 'fundsfiles'
            },{
                columnWidth: .06,
                style : 'text-align:center;',
                margin: '5 0 0 5',
                items: [
                    {
                        xtype: 'label',
                        itemId:'fundsfilescount',
                        text: '共0份'
                    }
                ]
            }, {
                columnWidth: .04,
                margin: '0 0 0 5',
                items: [
                    {
                        xtype: 'button',
                        itemId:'fundsfilesUpId',
                        text: '上传'
                    }
                ]
            },{
                columnWidth: .05,
                xtype: 'displayfield'
            },{
                columnWidth: .2,
                xtype: 'textfield',
                itemId:'setindexId',
                fieldLabel: '档案存放索引',
                labelWidth: 200,
                editable: false,
                name: 'setindex'
            },{
                columnWidth: .06,
                style : 'text-align:center;',
                margin: '5 0 0 5',
                items: [
                    {
                        xtype: 'label',
                        itemId:'setindexcount',
                        text: '共0份'
                    }
                ]
            }, {
                columnWidth: .04,
                margin: '0 0 0 5',
                items: [
                    {
                        xtype: 'button',
                        itemId:'setindexUpId',
                        text: '上传'
                    }
                ]
            },{
                columnWidth: .05,
                xtype: 'displayfield'
            },{
                columnWidth: .3,
                xtype: 'checkbox',
                itemId: 'isroomrecordId',
                name: 'isroomrecord',
                inputValue: '1',
                boxLabel: '是否坚持测记库房温度'
            },{
                columnWidth: .3,
                xtype: 'checkbox',
                itemId: 'ischeckrecordId',
                margin: '5 0 0 0',
                name: 'ischeckrecord',
                inputValue: '1',
                boxLabel: '是否定期检查档案并记录'
            },{
                columnWidth: .05,
                xtype: 'displayfield'
            },{
                columnWidth: .3,
                xtype: 'checkbox',
                itemId: 'isrepairId',
                margin: '5 0 0 0',
                name: 'isrepair',
                inputValue: '1',
                boxLabel: '是否对破损、霉变、褪变档案进行修复'
            }]
        },{
            columnWidth: 1,
            xtype: 'fieldset',
            title: "鉴定统计",
            margin: '0 10 0 10',
            layout: 'column',
            items: [{
                columnWidth: .3,
                xtype: 'checkbox',
                itemId: 'isauditrecordId',
                name: 'isauditrecord',
                inputValue: '1',
                boxLabel: '是否按期完成到期档案的鉴定工作并有鉴定记录'
            },{
                columnWidth: .05,
                xtype: 'displayfield'
            },{
                columnWidth: .3,
                xtype: 'checkbox',
                itemId: 'iscomplaterecordId',
                name: 'iscomplaterecord',
                inputValue: '1',
                boxLabel: '档案销毁手续是否完备'
            },{
                columnWidth: .05,
                xtype: 'displayfield'
            },{
                columnWidth: .3,
                xtype: 'checkbox',
                itemId: 'isrecordbookId',
                name: 'isrecordbook',
                inputValue: '1',
                boxLabel: '是否建有档案资料收进、移出登记簿'
            },{
                columnWidth: .3,
                xtype: 'checkbox',
                itemId: 'isparameterId',
                margin: '5 0 0 0',
                name: 'isparameter',
                inputValue: '1',
                boxLabel: '是否各类档案建立统计台账'
            },{
                columnWidth: .05,
                xtype: 'displayfield'
            },{
                columnWidth: .3,
                xtype: 'checkbox',
                itemId: 'isontimereportId',
                margin: '5 0 0 0',
                name: 'isontimereport',
                inputValue: '1',
                boxLabel: '是否按要求及时准确报送档案统计报表'
            }]
        },{
            columnWidth: 1,
            xtype: 'fieldset',
            title: "移交接收",
            margin: '0 10 0 10',
            layout: 'column',
            items: [{
                columnWidth: .3,
                fieldLabel: '本单位接收的档案类型',
                xtype: 'combo',
                name: 'receivetype',
                labelWidth: 200,
                multiSelect: true,
                store: receivetypeMode,
                displayField: 'text',
                valueField: 'value',
                queryMode: 'local'
            },{
                columnWidth: .05,
                xtype: 'displayfield'
            },{
                columnWidth: .3,
                xtype: 'checkbox',
                itemId: 'reciveograndataId',
                name: 'reciveograndata',
                inputValue: '1',
                boxLabel: '是否按规定及时接收机关部门各类档案及数据'
            },{
                columnWidth: .05,
                xtype: 'displayfield'
            },{
                columnWidth: .3,
                xtype: 'checkbox',
                itemId: 'tranferorgandataId',
                name: 'tranferorgandata',
                inputValue: '1',
                boxLabel: '是否按规定向市档案馆移交各类档案及其数据'
            },{
                columnWidth: .3,
                xtype: 'textfield',
                itemId: 'yeartranferId',
                fieldLabel: '档案移交（年度）',
                margin: '5 0 0 0',
                name: 'yeartranfer',
                labelWidth: 200
            },{
                columnWidth: .05,
                xtype: 'displayfield'
            },{
                columnWidth: .3,
                xtype: 'checkbox',
                itemId: 'iseleandpageId',
                margin: '5 0 0 0',
                name: 'iseleandpage',
                inputValue: '1',
                boxLabel: '是否按规定向市档案馆提供政府公开信息的纸质和电子文本'
            },{
                columnWidth: .05,
                xtype: 'displayfield'
            },{
                columnWidth: .3,
                xtype: 'checkbox',
                itemId: 'iscomplateproveId',
                margin: '5 0 0 0',
                name: 'iscomplateprove',
                inputValue: '1',
                boxLabel: '移交接收手续是否完备'
            },{
                columnWidth: .3,
                xtype: 'textfield',
                itemId: 'tranferkindId',
                fieldLabel: '档案移交（种类）',
                margin: '5 0 0 0',
                name: 'tranferkind',
                labelWidth: 200
            },{
                columnWidth: .05,
                xtype: 'displayfield'
            }, {
                columnWidth: .3,
                xtype: 'textfield',
                itemId: 'tranferfilenumId',
                fieldLabel: '档案移交（数量/件）',
                margin: '5 0 0 0',
                name: 'tranferfilenum',
                labelWidth: 200
            },{
                columnWidth: .05,
                xtype: 'displayfield'
            }, {
                columnWidth: .3,
                xtype: 'textfield',
                itemId: 'tranferfilesnumId',
                fieldLabel: '档案移交（数量/卷）',
                margin: '5 0 0 0',
                name: 'tranferfilesnum',
                labelWidth: 200
            }, {
                columnWidth: .3,
                xtype: 'textfield',
                itemId: 'yeareletranferId',
                fieldLabel: '文件移交（年度）',
                margin: '5 0 0 0',
                name: 'yeareletranfer',
                labelWidth: 200
            },{
                columnWidth: .05,
                xtype: 'displayfield'
            }, {
                columnWidth: .3,
                xtype: 'textfield',
                itemId: 'eletranfernumId',
                fieldLabel: '文件移交（数量/件）',
                margin: '5 0 0 0',
                name: 'eletranfernum',
                labelWidth: 200
            },{
                columnWidth: .05,
                xtype: 'displayfield'
            }, {
                columnWidth: .3,
                xtype: 'textfield',
                itemId: 'eletranferopennumId',
                fieldLabel: '文件移交（公开文件报送/件）',
                margin: '5 0 0 0',
                name: 'eletranferopennum',
                labelWidth: 200
            }]
        },{
            columnWidth: 1,
            xtype: 'fieldset',
            title: "监督指导",
            margin: '0 10 0 10',
            layout: 'column',
            items: [{
                columnWidth: .3,
                xtype: 'checkbox',
                itemId: 'islearnactiveId',
                name: 'islearnactive',
                inputValue: '1',
                boxLabel: '是否组织开展档案业务检查培训学习交流等活动'
            },{
                columnWidth: .05,
                xtype: 'displayfield'
            },{
                columnWidth: .3,
                xtype: 'checkbox',
                itemId: 'ismeetingId',
                name: 'ismeeting',
                inputValue: '1',
                boxLabel: '是否召开档案会议'
            },{
                columnWidth: .05,
                xtype: 'displayfield'
            },{
                columnWidth: .3,
                xtype: 'checkbox',
                itemId: 'isworktargetId',
                name: 'isworktarget',
                inputValue: '1',
                boxLabel: '是否实现档案工作目标'
            },{
                columnWidth: .2,
                xtype: 'textfield',
                itemId:'normativefilenameId',
                fieldLabel: '指定本系统档案工作的规范性文件',
                labelWidth: 200,
                editable: false,
                name: 'normativefilename',
                margin: '5 0 0 0'
            },{
                columnWidth: .06,
                style : 'text-align:center;',
                margin: '10 0 0 5',
                items: [
                    {
                        xtype: 'label',
                        itemId:'normativefilenamecount',
                        text: '共0份'
                    }
                ]
            }, {
                columnWidth: .04,
                margin: '5 0 0 5',
                items: [
                    {
                        xtype: 'button',
                        itemId:'normativefilenameUpId',
                        text: '上传'
                    }
                ]
            }]
        }]
    }],
    buttonAlign: 'center',
    buttons:[{
        text: '保存',
        itemId:'saveAllId'
    }]
});
