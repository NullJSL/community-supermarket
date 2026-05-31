const app = getApp();
const { post, put } = require('../../utils/request');

Page({
  data: {
    userInfo: null,
    isAdmin: false
  },

  onShow() {
    const userInfo = app.globalData.userInfo;
    this.setData({
      userInfo,
      isAdmin: userInfo?.role === 'ADMIN'
    });
  },

  async login() {
    wx.login({
      success: async (res) => {
        if (res.code) {
          try {
            const result = await post('/user/wx-login', {
              code: res.code,
              nickname: '微信用户',
              avatarUrl: ''
            });
            app.setToken(result.token);
            app.globalData.userInfo = result.user;
            this.setData({
              userInfo: result.user,
              isAdmin: result.user.role === 'ADMIN'
            });
            wx.showToast({ title: '登录成功', icon: 'success' });
          } catch (e) {
            console.error('登录失败', e);
          }
        }
      }
    });
  },

  goToOrders() {
    wx.navigateTo({ url: '/pages/order-list/order-list' });
  },

  goToAdminOrders() {
    wx.navigateTo({ url: '/pages/admin/order-manage/order-manage' });
  },

  goToInventory() {
    wx.navigateTo({ url: '/pages/admin/inventory/inventory' });
  },

  goToProductManage() {
    wx.navigateTo({ url: '/pages/admin/product-manage/product-manage' });
  },

  goToSalesReport() {
    wx.navigateTo({ url: '/pages/admin/sales-report/sales-report' });
  },

  logout() {
    app.logout();
    this.setData({ userInfo: null, isAdmin: false });
    wx.showToast({ title: '已退出', icon: 'success' });
  }
});
