const cart = require('../../utils/cart');
const { post } = require('../../utils/request');

Page({
  data: {
    cartItems: [],
    totalPrice: '0.00',
    address: '',
    phone: '',
    remark: ''
  },

  onLoad() {
    const cartItems = cart.getCartItems();
    const { total } = cart.getCartTotal();
    const app = getApp();
    this.setData({
      cartItems,
      totalPrice: total,
      address: app.globalData.userInfo?.address || '',
      phone: app.globalData.userInfo?.phone || ''
    });
  },

  onAddressInput(e) { this.setData({ address: e.detail.value }); },
  onPhoneInput(e) { this.setData({ phone: e.detail.value }); },
  onRemarkInput(e) { this.setData({ remark: e.detail.value }); },

  async submitOrder() {
    const { address, phone, remark, cartItems } = this.data;

    if (!address) {
      wx.showToast({ title: '请输入配送地址', icon: 'none' });
      return;
    }
    if (!phone) {
      wx.showToast({ title: '请输入联系电话', icon: 'none' });
      return;
    }

    try {
      const orderData = {
        deliveryAddress: address,
        deliveryPhone: phone,
        remark,
        items: cartItems.map(item => ({
          productId: item.productId,
          quantity: item.quantity
        }))
      };

      const order = await post('/order', orderData);
      cart.clearCart();

      wx.showToast({ title: '下单成功', icon: 'success' });
      setTimeout(() => {
        wx.redirectTo({ url: `/pages/order-detail/order-detail?id=${order.id}` });
      }, 1500);
    } catch (e) {
      console.error('下单失败', e);
    }
  }
});
