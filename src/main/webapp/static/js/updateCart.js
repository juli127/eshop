// scripts for 'product' page
function plus(productId) {
    var elem = document.getElementById('pq' + productId);
    elem.innerHTML = +elem.innerHTML + 1;
}

function minus(productId) {
    var elem = document.getElementById('pq' + productId);
    if (elem.innerHTML > 0) {
        elem.innerHTML = +elem.innerHTML - 1;
    }
}

function buy(userId, productId) {
    if (userId == null || userId == ""){
        alert("You should register or login before shopping!");
    } else {
        var elem = document.getElementById('pq' + productId);
        var qnt = +elem.innerHTML;
        $.ajax({
            type: "POST",
            url: "./cart",
            data: {
                'action':'add',
                'productId': productId,
                'quantity' : qnt
            },
            dataType: 'json',
            success: function (response) {
                parseRespose(response);
            },
            error: function(e) {
                console.log(e.message);
            }
        });
        alert(qnt + " items was added to your cart");
    }
}

// scripts for 'cart' page
function deleteFromCart(productId) {
    var elem = document.getElementById('q' + productId);
    var qnt = +elem.innerHTML;
    if (elem.innerHTML > 0) {
        elem.innerHTML = qnt - 1;
        $.ajax({
            type: "POST",
            url: "./cart",
            data: {
                'action':'remove',
                'productId': productId,
                'quantity': 1
            },
            dataType: 'json',
            success: function (response) {
                parseRespose(response);
            },
            error: function(e) {
                console.log(e.message);
            }
        });
    }
}

function addToCart(productId) {
    var elem = document.getElementById('q' + productId);
    elem.innerHTML = +elem.innerHTML + 1;
    $.ajax({
        type: "POST",
        url: "./cart",
        data: {
            'action': 'add',
            'productId': productId,
            'quantity': 1
        },
        dataType: 'json',
        success: function (response) {
            parseRespose(response);
        },
        error: function(e) {
            console.log(e.message);
        }
    });
}

// got JSON string from /cart servlet
function parseRespose(responce) {
    var responceJSON = JSON.stringify(responce);
    // alert('got JSON from server: '+ responceJSON);
    var responceJSON = JSON.parse(responceJSON);
    document.getElementById('goodsCount').innerHTML = responceJSON.cartSize;
    document.getElementById('TotalSum').innerHTML = responceJSON.totalSum;
    // var updProdcustsMap = new Map();
    // for (i=0; i < responceJSON.products.length; i++){
    //     updProdcust[i].productId = responceJSON.products[i].key.name;
    //     updProdcust[i].productId = responceJSON.products[i].productId;
    // }
    // alert('got updated Cart products: '+ responceJSON.products);
}

function makeOrder(userId) {
    var qnt = document.getElementById('q' + userId).innerHTML;
    $.ajax({
        type: "POST",
        url: "./order",
        data: {
            'action':'makeOrder',
            'userID':userId
        },
        dataType: 'json',
        success: function (response) {
            alert('Order has been created');
        },
        error: function(e) {
            console.log(e.message);
        }
    });
}
