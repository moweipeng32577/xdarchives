/**
 * Created by yl on 2019/1/10.
 * 查看数据包
 */
Ext.define('LongRetention.view.LongRetentionPackageView',{
    extend:'Ext.window.Window',
    xtype:'packageWindow',
    width:'80%',
    height:'70%',
    modal:true,
    closeToolText:'关闭',
    title:'查看封装包',
    layout: 'border',
    items: [{
        region: 'west',
        width: '30%',
        xtype: 'treepanel',
        itemId: 'treepanelId',
        autoScroll: true,
        containerScroll: true,
        split:true,
        header: false,
        hideHeaders: true,
        rootVisible: false,
        folderSort : true ,
        store: {
            model: 'LongRetention.model.LongRetentionResultGridModel',
            proxy: {
                type: 'ajax',
                url: '/longRetention/showVerifyPackage',
                extraParams: {entryid:''},
                reader: {
                    type: 'json',
                    expanded: true
                }
            },
            listeners: {
                beforeload: function (){
                    this.proxy.extraParams.entryid = window.entryid;
                }
            }
        }
    }, {
        xtype: 'panel',
        region: 'center',
        split: true,
        layout: 'fit',
        items:{
            xtype: 'dataview',
            itemId:'dataview',
            reference: 'dataview',
            trackOver: true,
            overItemCls: 'x-item-over',
            draggable: false,
            autoScroll: true,
            plugins: [
                {
                    ptype: 'ux-animated-dataview'
                },
                Ext.create('Ext.ux.DataView.DragSelector', {}),
                Ext.create('Ext.ux.DataView.LabelEditor', {dataIndex: 'name'})
            ],
            store:{
                fields: ['name', 'url'],
                proxy: {
                    type: 'ajax',
                    url: '/longRetention/getDataView',
                    extraParams: {
                        childrens:''
                    },
                    reader: {
                        type: 'json',
                        rootProperty: 'content'
                    }
                }
            },

            prepareData: function (data) {
                Ext.apply(data, {
                    shortName: Ext.util.Format.ellipsis(data.name,10),
                    sizeString: Ext.util.Format.fileSize(data.size),
                    dateString: Ext.util.Format.date(data.lastmod, "m/d/Y g:i a")
                });
                return data;
            },
            selectionModel: {
                mode: 'MULTI'
            },
            itemSelector: 'div.thumb-wrap',

            tpl: [
                '<tpl for=".">',
                '<div class="thumb-wrap" style="float:left; margin:10px;" id="{name}" >',
                '<center><div class="thumb"><img width="80px" height="80px" src="{url}" /></div>',
                '<span>{shortName}</span></center>',
                '</div>',
                '</tpl>',
                '<div class="x-clear"></div>'
            ],

            listeners: {
                itemclick: function (view, item) {
                },
                selectionchange: function (dv, nodes) {
                },
                itemdblclick: function (view, item) {
                    var url = item.data.url;
                    if(url.indexOf('folder.png')>-1){
                        // var childrens = [];
                        // for (var i = 0; i < record.get('children').length; i++) {
                        //     childrens.push(record.get('children')[i].text);
                        // }
                        // view.getStore().proxy.extraParams.childrens = childrens;
                        // view.getStore().reload();
                    }else{

                    }
                    // var fileName = item.data.name;
                    // window.open("/exchangeStorage/openSipFile?fileName=" + fileName);
                },
                itemmouseenter : function (view, index, target, record, e, eOpts ) {
                    if (view.tip == null) {
                        view.tip = Ext.create('Ext.tip.ToolTip', {
                            target: view.el,
                            delegate: view.itemSelector,
                            renderTo: Ext.getBody()

                        });
                    };
                    view.el.clean();
                    view.tip.update(index.data.name);
                }
            }
        }
    },{
        region: 'east',
        width: '32%',
        split: true,
        xtype:'panel',
        autoScroll:true,
        layout: {
            align: 'middle',
            pack: 'center',
            type: 'hbox'
        },
        items:{
            xtype: 'fieldset',
            title: '元数据',
            style:'background:#fff;padding-top:0px;',
            items:{
                xtype: 'form',
                itemId:'metadataForm',
                items:[
                    {xtype: 'textfield',readOnly:true, fieldLabel: '题名<span style="color:red">(必填)</span>',name:'题名'},
                    {xtype: 'textfield',readOnly:true, fieldLabel: '档号<span style="color:red">(必填)</span>',name:'档号'},
                    {xtype: 'textfield',readOnly:true, fieldLabel: '归档年度<span style="color:red">(必填)</span>',name:'归档年度'},
                    {xtype: 'textfield',readOnly:true, fieldLabel: '文件日期<span style="color:red">(必填)</span>',name:'文件日期'},
                    {xtype: 'textfield',readOnly:true, fieldLabel: '密级(<span style="color:red">(必填)</span>',name:'密级'},
                    {xtype: 'textfield',readOnly:true, fieldLabel: '保管期限<span style="color:red">(必填)</span>',name:'保管期限'},
                    {xtype: 'textfield',readOnly:true, fieldLabel: '责任者<span style="color:red">(必填)</span>',name:'责任者'},
                    {xtype: 'textfield',readOnly:true, fieldLabel: '录入人',name:'录入人'},
                    {xtype: 'textfield',readOnly:true, fieldLabel: '录入时间',name:'录入时间'},
                    {xtype: 'textfield',readOnly:true, fieldLabel: '文件编号',name:'文件编号'},
                    {xtype: 'textfield',readOnly:true, fieldLabel: '电子文件名',name:'文件名称'},
                    {xtype: 'textfield',readOnly:true, fieldLabel: '开放审核',name:'开放审核'},
                    {xtype: 'textfield',readOnly:true, fieldLabel: '归类',name:'报文类别名称'},
                    {xtype: 'textfield',readOnly:true, fieldLabel: '文件形成单位名称',name:'文件形成单位名称'},
                    {xtype: 'textfield',readOnly:true, fieldLabel: '主题词',name:'主题或关键词'},
                    {xtype: 'textfield',readOnly:true, fieldLabel: '档案馆代码',name:'reduceratio'},
                    {xtype: 'textfield',readOnly:true, fieldLabel: '最后修改时间',name:'levelresolution'},
                    {xtype: 'textfield',readOnly:true, fieldLabel: '文件大小',name:'文件大小'},
                    {xtype: 'textfield',readOnly:true, fieldLabel: '文件字数',name:'equipmenttype'},
                    {xtype: 'textfield',readOnly:true, fieldLabel: '归档操作者',name:'equipmentmanufacturer'},
                    {xtype: 'textfield',readOnly:true, fieldLabel: '接收单位名称',name:'equipmentmodel'},
                    {xtype: 'textfield',readOnly:true, fieldLabel: '类别与格式',name:'equipmentsensitization'},
                    {xtype: 'textfield',readOnly:true, fieldLabel: '应用软件环境',name:'equipmentmodel'},
                    {xtype: 'textfield',readOnly:true, fieldLabel: '系统环境',name:'equipmentsensitization'}
                ]
            }
        }
    }],
    buttons: [{
        text: '返回', handler: function (view) {
            view.up('packageWindow').close();
        }
    }]
})
