/**
 * Created by Administrator on 2020/3/18.
 */


Ext.define('Feedback.view.AppraiseView', {
    extend: 'Ext.window.Window',
    xtype: 'appraiseView',
    itemId: 'appraiseViewId',
    title: '设置评分',
    frame: true,
    resizable: true,
    width: 460,
    height: 300,
    modal: true,
    closeToolText: '关闭',
    layout: "border",
    items: [
        {
            region: 'north',
            height: 35,
            layout: 'fit',
            items: [{
                xtype: 'label',
                text: "5-无可挑剔",
                itemId: "labelId",
                margin: "15 0 0 190",
                style:"color: red;font-size: 18px !important"
            }]
        }, {
            region: 'center',
            layout: 'fit',
            height: 50,
            items: [{
                itemId: 'setAppraiseId',
                hideMode: 'visibility',
                html:"<span>"
                +"<img id='starone' src='../img/star2.png' height='40' width='40'  alt='yes'> "
                +"<img id='startwo' src='../img/star2.png' height='40' width='40'  alt='yes'> "
                +"<img id='starthree' src='../img/star2.png' height='40' width='40'  alt='yes'> "
                +"<img id='starfour' src='../img/star2.png' height='40' width='40'  alt='yes'> "
                +"<img id='starfive' src='../img/star2.png' height='40' width='40'  alt='yes'> "
                +"</span>",
                margin: "25 0 0 130"
            }]
        }, {
            region: 'south',
            layout: 'fit',
            items: [{
                itemId: 'contentId',
                xtype: 'textarea',
                fieldLabel: '反馈意见',
                name: 'content',
                margin: "0 5 5 0",
                emptyText: "您的反馈回督促我们做得更好"
            }]
        }
    ],
    listeners:{
        render:function (view) {
            var imgone = document.querySelector('#starone');
            var imgtwo = document.querySelector('#startwo');
            var imgthree = document.querySelector('#starthree');
            var imgfour = document.querySelector('#starfour');
            var imgfive = document.querySelector('#starfive');
            imgone.addEventListener('click', function() {
                setAppraise(1,view);
            });
            imgtwo.addEventListener('click', function() {
                setAppraise(2,view);
            });
            imgthree.addEventListener('click', function() {
                setAppraise(3,view);
            });
            imgfour.addEventListener('click', function() {
                setAppraise(4,view);
            });
            imgfive.addEventListener('click', function() {
                setAppraise(5,view);
            });
        }
    },

    buttons: [{
        text: '提交',
        itemId: 'setAppraiseSubmit'
    }, {
        text: '关闭',
        itemId: 'setAppraiseClose'
    }]
});


//评分点击星星
function setAppraise(type,view) {
    var imgtwo = document.querySelector('#startwo');
    var imgthree = document.querySelector('#starthree');
    var imgfour = document.querySelector('#starfour');
    var imgfive = document.querySelector('#starfive');
    var alttwo = imgtwo.getAttribute("alt");
    var altthree = imgthree.getAttribute("alt");
    var altfour = imgfour.getAttribute("alt");
    var altfive = imgfive.getAttribute("alt");
    var label = view.down('[itemId=labelId]');
    if(type==1){
        imgtwo.setAttribute("src","../img/star1.png");
        imgtwo.setAttribute("alt","no");
        imgthree.setAttribute("src","../img/star1.png");
        imgthree.setAttribute("alt","no");
        imgfour.setAttribute("src","../img/star1.png");
        imgfour.setAttribute("alt","no");
        imgfive.setAttribute("src","../img/star1.png");
        imgfive.setAttribute("alt","no");
        label.setText("1-很差");
    }else if(type==2){
        if(alttwo=="yes"){
            imgtwo.setAttribute("src","../img/star1.png");
            imgthree.setAttribute("src","../img/star1.png");
            imgfour.setAttribute("src","../img/star1.png");
            imgfive.setAttribute("src","../img/star1.png");
            imgtwo.setAttribute("alt","no");
            imgthree.setAttribute("alt","no");
            imgfour.setAttribute("alt","no");
            imgfive.setAttribute("alt","no");
            label.setText("1-很差");
        }else{
            imgtwo.setAttribute("src","../img/star2.png");
            imgtwo.setAttribute("alt","yes");
            label.setText("2-一般");
        }
    }else if(type==3){
        if(altthree=="yes"){
            imgthree.setAttribute("src","../img/star1.png");
            imgfour.setAttribute("src","../img/star1.png");
            imgfive.setAttribute("src","../img/star1.png");
            imgthree.setAttribute("alt","no");
            imgfour.setAttribute("alt","no");
            imgfive.setAttribute("alt","no");
            label.setText("2-一般");
        }else{
            imgtwo.setAttribute("src","../img/star2.png");
            imgthree.setAttribute("src","../img/star2.png");
            imgtwo.setAttribute("alt","yes");
            imgthree.setAttribute("alt","yes");
            label.setText("3-满意");
        }
    }else if(type==4){
        if(altfour=="yes"){
            imgfour.setAttribute("src","../img/star1.png");
            imgfive.setAttribute("src","../img/star1.png");
            imgfour.setAttribute("alt","no");
            imgfive.setAttribute("alt","no");
            label.setText("3-满意");
        }else{
            imgtwo.setAttribute("src","../img/star2.png");
            imgthree.setAttribute("src","../img/star2.png");
            imgfour.setAttribute("src","../img/star2.png");
            imgtwo.setAttribute("alt","yes");
            imgthree.setAttribute("alt","yes");
            imgfour.setAttribute("alt","yes");
            label.setText("4-非常满意");
        }
    }else if(type==5){
        if(altfive=="yes"){
            imgfive.setAttribute("src","../img/star1.png");
            imgfive.setAttribute("alt","no");
            label.setText("4-非常满意");
        }else{
            imgtwo.setAttribute("src","../img/star2.png");
            imgthree.setAttribute("src","../img/star2.png");
            imgfour.setAttribute("src","../img/star2.png");
            imgfive.setAttribute("src","../img/star2.png");
            imgtwo.setAttribute("alt","yes");
            imgthree.setAttribute("alt","yes");
            imgfour.setAttribute("alt","yes");
            imgfive.setAttribute("alt","yes");
            label.setText("5-无可挑剔");
        }
    }
}
