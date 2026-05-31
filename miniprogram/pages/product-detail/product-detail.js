const { get } = require('../../utils/request');
const cart = require('../../utils/cart');

Page({
  data: {
    product: null,
    quantity: 1
  },

  onLoad(options) {
    if (options.id) {
      this.loadProduct(options.id);
    }
  },

  async loadProduct(id) {
    try {
      const product = await get(`/product/${id}`);
      this.setData({ product });
    } catch (e) {
      console.error('加载商品详情失败', e);
    }
  },

  onQuantityChange(e) {
    const type = e.currentTarget.dataset.type;
    let quantity = this.data.quantity;
    if (type === 'minus' && quantity > 1) {
      quantity--;
    } else if (type === 'plus') {
      quantity++;
    }
    this.setData({ quantity });
  },

  addToCart() {
    const { product, quantity } = this.data;
    cart.addToCart(product, quantity);
    wx.showToast({ title: '已加入购物车', icon: 'success' });
  },

  buyNow() {
    const { product, quantity } = this.data;
    cart.addToCart(product, quantity);
    wx.switchTab({ url: '/pages/cart/cart' });
  }
});
