const cart = require('../../utils/cart');

Page({
  data: {
    cartItems: [],
    totalPrice: '0.00',
    totalCount: 0
  },

  onShow() {
    this.refreshCart();
  },

  refreshCart() {
    const cartItems = cart.getCartItems();
    const { total, count } = cart.getCartTotal();
    this.setData({ cartItems, totalPrice: total, totalCount: count });
  },

  onQuantityChange(e) {
    const { id, type } = e.currentTarget.dataset;
    const items = cart.getCartItems();
    const item = items.find(i => i.productId === id);
    if (!item) return;

    let newQty = item.quantity;
    if (type === 'minus') newQty--;
    else newQty++;

    cart.updateCartItemQuantity(id, newQty);
    this.refreshCart();
  },

  removeItem(e) {
    const id = e.currentTarget.dataset.id;
    wx.showModal({
      title: '确认删除',
      content: '确定要从购物车中移除此商品吗？',
      success: (res) => {
        if (res.confirm) {
          cart.removeFromCart(id);
          this.refreshCart();
        }
      }
    });
  },

  clearAll() {
    wx.showModal({
      title: '清空购物车',
      content: '确定要清空购物车吗？',
      success: (res) => {
        if (res.confirm) {
          cart.clearCart();
          this.refreshCart();
        }
      }
    });
  },

  checkout() {
    if (this.data.cartItems.length === 0) {
      wx.showToast({ title: '购物车为空', icon: 'none' });
      return;
    }
    wx.navigateTo({ url: '/pages/order-confirm/order-confirm' });
  }
});
