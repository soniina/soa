export class HeroesApi {
  constructor(getBaseUrl) {
    this.getBaseUrl = getBaseUrl;
  }

  async removeHeroFromTeam(teamId, heroId) {
    const res = await fetch(`${this.getBaseUrl()}/heroes/team/${teamId}/remove/${heroId}`, {
      method: 'DELETE'
    });
    if (!res.ok) throw await this._extractError(res);
  }

  async addCarToTeam(teamId) {
    const res = await fetch(`${this.getBaseUrl()}/heroes/team/${teamId}/car/add`, {
      method: 'POST'
    });
    if (!res.ok) throw await this._extractError(res);
    return await res.json();
  }

  async _extractError(res) {
    const data = await res.json().catch(() => ({}));
    return {
      status: res.status,
      message: data.message || `Ошибка Сервиса 2 (${res.status})`,
      violations: data.violations || []
    };
  }
}