const { get } = require('../../../utils/request');

Page({
  data: {
    report: null,
    startDate: '',
    endDate: ''
  },

  onLoad() {
    const today = new Date();
    const endDate = today.toISOString().split('T')[0];
    const startDate = new Date(today.setDate(today.getDate() - 7)).toISOString().split('T')[0];
    this.setData({ startDate, endDate });
    this.loadReport();
  },

  async loadReport() {
    try {
      const report = await get('/admin/stats/sales', {
        startDate: this.data.startDate,
        endDate: this.data.endDate
      });
      this.setData({ report });
    } catch (e) { console.error(e); }
  },

  onStartDateChange(e) {
    this.setData({ startDate: e.detail.value });
    this.loadReport();
  },

  onEndDateChange(e) {
    this.setData({ endDate: e.detail.value });
    this.loadReport();
  }
});
