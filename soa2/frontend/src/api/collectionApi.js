export class CollectionApi {
  constructor(getBaseUrl) {
    this.getBaseUrl = getBaseUrl;
  }

  async getHeroes(params) {
    const query = new URLSearchParams();
    query.append('page', params.page || 1);
    query.append('size', params.size || 10);
    query.append('sort', `${params.sortBy || 'id'},${params.sortDir || 'asc'}`);

    if (params.filters) {
      for (const [key, val] of Object.entries(params.filters)) {
        if (val !== '' && val !== null && val !== undefined) {
          query.append(key, val);
        }
      }
    }

    const res = await fetch(`${this.getBaseUrl()}/human-beings?${query.toString()}`);
    if (!res.ok) throw await this._extractError(res);

    const totalCount = res.headers.get('X-Total-Count');
    const items = await res.json();
    return { items, totalCount: totalCount ? parseInt(totalCount, 10) : items.length };
  }

  async createHero(hero) {
    const res = await fetch(`${this.getBaseUrl()}/human-beings`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(hero)
    });
    if (!res.ok) throw await this._extractError(res);
    return await res.json();
  }

  async updateHero(id, hero) {
    const res = await fetch(`${this.getBaseUrl()}/human-beings/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(hero)
    });
    if (!res.ok) throw await this._extractError(res);
    return await res.json();
  }

  async deleteHero(id) {
    const res = await fetch(`${this.getBaseUrl()}/human-beings/${id}`, { method: 'DELETE' });
    if (!res.ok) throw await this._extractError(res);
  }

  async countMoodLessThan(mood) {
    const res = await fetch(`${this.getBaseUrl()}/human-beings/count/mood-less-than?mood=${mood}`);
    if (!res.ok) throw await this._extractError(res);
    return await res.json();
  }

  async searchContains(substring) {
    const res = await fetch(`${this.getBaseUrl()}/human-beings/search/name-contains?substring=${encodeURIComponent(substring)}`);
    if (!res.ok) throw await this._extractError(res);
    return await res.json();
  }

  async searchPrefix(prefix) {
    const res = await fetch(`${this.getBaseUrl()}/human-beings/search/name-prefix?prefix=${encodeURIComponent(prefix)}`);
    if (!res.ok) throw await this._extractError(res);
    return await res.json();
  }

  async _extractError(res) {
    const data = await res.json().catch(() => ({}));
    return {
      status: res.status,
      message: data.message || `Ошибка сервера (${res.status})`,
      violations: data.violations || []
    };
  }
}