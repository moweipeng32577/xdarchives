Ext.define('ThematicUtilize.view.ThematicUtilizeWindow', {
    extend: 'Ext.window.Window',
    xtype: 'thematicUtilizeWindow',
    title: '增加',
    width: 460,
    height: 410,
    closeToolText: '关闭',
    modal: true,
    resizable: false,
    layout: 'fit',
    items: [
        {
            xtype: 'form',
            layout: 'column',
            bodyPadding: 20,
            items: [{
                xtype: 'textfield',
                fieldLabel: '专题名称<span style="color: #CC3300; padding-right: 2px;">*</span>',
                columnWidth: 1,
                allowBlank: false,
                blankText: '该输入项为必输项',
                name: 'title',
                labelWidth: 100
            }, {
                xtype: 'textarea',
                height: 135,
                fieldLabel: '专题描述<span style="color: #CC3300; padding-right: 2px;">*</span>',
                columnWidth: 1,
                allowBlank: false,
                blankText: '该输入项为必输项',
                name: 'thematiccontent',
                labelWidth: 100,
                margin: '10 0 0 0'
            }, {
                xtype : 'combobox',
                name:'thematictypes',
                store: Ext.create('Ext.data.Store',{
                    proxy: {
                        type:'ajax',
                        url:'/systemconfig/getByConfigcode',
                        extraParams:{configcode:'方志馆'},
                        reader: {
                            type:'json'
                        }
                    },
                    autoLoad:true
                }),
                    fieldLabel:'专题类型<span style="color: #CC3300; padding-right: 2px;">*</span>',
                    columnWidth: 1,
                    blankText:'该输入项为必输项',
                    editable:false,
                    allowBlank: false,
                    margin: '10 0 0 0',
                    displayField: "text",
                    valueField: "text"
                },
                {
                    columnWidth: 0.27,
                    xtype: 'label',
                    text: '背景图:',
                    margin: '20 0 0 0'
                },
                {
                    xtype: 'component', //或者xtype: 'component',
                    width: 65, //图片宽度
                    height: 65, //图片高度
                    itemId:'component',
                    autoEl: {
                        tag: 'img',    //指定为img标签
                        src: '/img/icon/thematic_def.png'    //指定url路径
                    },
                    margin: '10 0 0 0',
                    backgroundpath:null,
                    listeners: {
                        el:{
                            click:function(){
                                var component =this.component;
                                var win = Ext.create('Comps.view.UploadBackgroundView', {parentByType:component});
                                win.on('close', function (view) {
                                    if(component.backgroundpath==null){
                                        component.getEl().dom.src ="/thematicProd/getBackground?url="+'/static/img/icon/thematic_def.png';
                                    }else{
                                        component.getEl().dom.src = "/thematicProd/getBackground?url=" + encodeURIComponent(component.backgroundpath);
                                    }
                                }, this);
                                win.show();
                            }
                        }
                    }
                },{
                    columnWidth: 0.6,
                    xtype: 'label',
                    text: '(点击图片可以进行上传，如果不上传，默认以当前图片为背景图)',
                    margin: '25 0 0 10'
                }
            ]
        }
    ],
    buttons: [{
        itemId: 'thematicProSaveBtnID',
        text: '保存'
    }, {
        itemId: 'thematicProBackBtnID',
        text: '返回'
    }]
});