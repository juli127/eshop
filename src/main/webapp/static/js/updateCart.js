// scripts for 'product' page
function plus(productId) {
    alert('plus: productId == null ? ' + (productId == null));
    alert('plus: from form == ' + (document.getElementById('productId').innerText));
    var elem = document.getElementById('pq' + productId);
    var qnt = +elem.innerHTML + 1;
    // alert('plus: qnt = ' + qnt);
    elem.innerHTML = qnt;
}

function minus(productId) {
    alert('minus: productId == null ? ' + (productId == null));
    var elem = document.getElementById('pq' + productId);
    var qnt = +elem.innerHTML;
    if (elem.innerHTML > 0) {
        elem.innerHTML = qnt - 1;
    }
    // alert('minus: qnt = ' + qnt);
}

function buy(userId, productId) {
    var elem = document.getElementById('pq' + productId);
    var qnt = +elem.innerHTML;
    if (userId == null){
        alert("You should register or login before shopping!");
    } else {
        // alert('user.id=' + userid);
        // if (userid == null || userid.equals("")) {
        //     alert("Login or register, please, to buy something!");
        // } else {
        alert("Buy " + qnt + " items of product with Id " + productId);
        $.ajax({
            type: "POST",
            url: "./cart",
            data: {'action':'addPurchase','productId':productId, 'quantity' :qnt},
            dataType: 'json',
            success: function (response) {
                parseRespose(response);
            }
        });
        alert(" this item was added to your cart");
        // }
    }
}

// scripts for 'cart' page
function deleteFromCart(productId) {
    var elem = document.getElementById('q' + productId);
    var qnt = +elem.innerHTML;
    if (elem.innerHTML > 0) {
        elem.innerHTML = qnt - 1;
        // TODO: change request to JSON format
        $.ajax({
            type: "POST",
            url: "./cart",
            data: {
                'action':'removePurchase',
                'productId':productId,
                'quantity' :1
            },
            dataType: 'json',
            // data: { removePurchase : productId + ":" + qnt },
            success: function (response) {
                parseRespose(response);
            }
        });
    }
}

function addToCart(productId) {
    var elem = document.getElementById('q' + productId);
    var qnt = +elem.innerHTML + 1;
    elem.innerHTML = qnt;
    // TODO: change request to JSON format
    $.ajax({
        type: "POST",
        url: "./cart",
        data: {
            'action': 'addPurchase',
            'productId': productId,
            'quantity': 1
        },
        dataType: 'json',
        success: function (response) {
            parseRespose(response);
        }
    });
}

function parseRespose(response) {
    // TODO: parse responce as JSON format (got it from CartServlet.java)
    alert("got responce from server: " + response);

    var json = $.parseJSON(response);



    var respData = response.toString().split("<br>");
    for (var i = 0; i < respData.length; i++) {
        if (respData[i].startsWith("header:")) {
            var start = respData[i].indexOf("header: ");
            if (start >= 0) {
                respData[i] = respData[i].substring(start + 8).trim();
                if (respData[i].startsWith("totalSum:")) {
                    alert("got from server TotalSum: " + respData[i].substring(start + 9).trim());
                    document.getElementById('TotalSum').innerHTML = respData[i].substring(start + 9).trim();
                }
                if (respData[i].startsWith("cartSize:")) {
                    alert("got from server cartSize: " + respData[i].substring(start + 9).trim());
                    document.getElementById('goodsCount').innerHTML = respData[i].substring(start + 9).trim();
                }
            }
        }
    }
}

function makeOrder(userId) {
    var qnt = document.getElementById('q' + userId).innerHTML;
    $.ajax({
        type: "POST",
        url: "./order",
        data: {
            'action':'makeOrder'},
        dataType: 'json',
        success: function (response) {
            alert('Заказ оформлен');
        }
    });
}