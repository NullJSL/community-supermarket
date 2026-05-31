const CART_KEY = 'cart_items';

function getCartItems() {
  return wx.getStorageSync(CART_KEY) || [];
}

function addToCart(product, quantity = 1) {
  const items = getCartItems();
  const index = items.findIndex(item => item.productId === product.id);

  if (index > -1) {
    items[index].quantity += quantity;
  } else {
    items.push({
      productId: product.id,
      name: product.name,
      price: product.price,
      imageUrl: product.imageUrl,
      unit: product.unit,
      quantity
    });
  }

  wx.setStorageSync(CART_KEY, items);
  return items;
}

function updateCartItemQuantity(productId, quantity) {
  const items = getCartItems();
  const index = items.findIndex(item => item.productId === productId);

  if (index > -1) {
    if (quantity <= 0) {
      items.splice(index, 1);
    } else {
      items[index].quantity = quantity;
    }
  }

  wx.setStorageSync(CART_KEY, items);
  return items;
}

function removeFromCart(productId) {
  const items = getCartItems().filter(item => item.productId !== productId);
  wx.setStorageSync(CART_KEY, items);
  return items;
}

function clearCart() {
  wx.setStorageSync(CART_KEY, []);
}

function getCartTotal() {
  const items = getCartItems();
  let total = 0;
  let count = 0;
  items.forEach(item => {
    total += item.price * item.quantity;
    count += item.quantity;
  });
  return { total: total.toFixed(2), count };
}

module.exports = { getCartItems, addToCart, updateCartItemQuantity, removeFromCart, clearCart, getCartTotal };
