<template>
  <div class="modal fade" id="heroModal" tabindex="-1" ref="modalRef">
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">{{ isEdit ? 'Редактировать героя #' + hero.id : 'Создать героя' }}</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
        </div>
        <form @submit.prevent="handleSubmit">
          <div class="modal-body">
            <div class="mb-3">
              <label class="form-label small">Имя *</label>
              <input type="text" class="form-control" v-model="localHero.name" required>
            </div>
            <div class="row g-2 mb-3">
              <div class="col-6">
                <label class="form-label small">X (макс. 740) *</label>
                <input type="number" step="any" max="740" class="form-control" v-model.number="localHero.coordinates.x" required>
              </div>
              <div class="col-6">
                <label class="form-label small">Y (макс. 913) *</label>
                <input type="number" step="any" max="913" class="form-control" v-model.number="localHero.coordinates.y" required>
              </div>
            </div>
            <div class="mb-3">
              <label class="form-label small">Скорость (> -193) *</label>
              <input type="number" step="any" min="-192.99" class="form-control" v-model.number="localHero.impactSpeed" required>
            </div>
            <div class="row g-2 mb-3">
              <div class="col-6">
                <label class="form-label small">Оружие</label>
                <select class="form-select" v-model="localHero.weaponType">
                  <option :value="null">Без оружия</option>
                  <option value="AXE">AXE</option>
                  <option value="PISTOL">PISTOL</option>
                  <option value="KNIFE">KNIFE</option>
                  <option value="BAT">BAT</option>
                </select>
              </div>
              <div class="col-6">
                <label class="form-label small">Настроение *</label>
                <select class="form-select" v-model="localHero.mood" required>
                  <option value="LONGING">LONGING</option>
                  <option value="GLOOM">GLOOM</option>
                  <option value="APATHY">APATHY</option>
                  <option value="FRENZY">FRENZY</option>
                </select>
              </div>
            </div>

            <!-- БЛОК АВТОМОБИЛЯ -->
            <div class="row g-2 mb-3">
              <div class="col-8">
                <label class="form-label small">Название машины (опционально)</label>
                <input
                        type="text"
                        class="form-control"
                        v-model="carName"
                        placeholder="Оставьте пустым, если машины нет"
                >
              </div>
              <div class="col-4 d-flex align-items-center pt-3">
                <div class="form-check">
                  <input
                          class="form-check-input"
                          type="checkbox"
                          v-model="carCool"
                          id="carCool"
                          :disabled="!carName || !carName.trim()"
                  >
                  <label class="form-check-label small" for="carCool">Крутая?</label>
                </div>
              </div>
            </div>

            <div class="d-flex gap-3">
              <div class="form-check">
                <input class="form-check-input" type="checkbox" v-model="localHero.realHero" id="realHero">
                <label class="form-check-label small" for="realHero">Настоящий герой</label>
              </div>
              <div class="form-check">
                <input class="form-check-input" type="checkbox" v-model="localHero.hasToothpick" id="toothpick">
                <label class="form-check-label small" for="toothpick">Зубочистка</label>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Отмена</button>
            <button type="submit" class="btn btn-success">{{ isEdit ? 'Сохранить' : 'Создать' }}</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
  import { Modal } from 'bootstrap';

  export default {
    props: { hero: Object, isEdit: Boolean },
    emits: ['save'],
    data() {
      return {
        localHero: JSON.parse(JSON.stringify(this.hero || {})),
        carName: '',
        carCool: false
      };
    },
    watch: {
      hero: {
        deep: true,
        handler(newVal) {
          this.localHero = JSON.parse(JSON.stringify(newVal || {}));
          if (newVal && newVal.car && newVal.car.name) {
            this.carName = newVal.car.name;
            this.carCool = !!newVal.car.cool;
          } else {
            this.carName = '';
            this.carCool = false;
          }
        }
      }
    },
    mounted() {
      this.bsModal = new Modal(this.$refs.modalRef);
    },
    methods: {
      show() { this.bsModal.show(); },
      hide() { this.bsModal.hide(); },
      handleSubmit() {
        const heroToSave = JSON.parse(JSON.stringify(this.localHero));

        // если поле пустое, машины нет (car = null)
        if (this.carName && this.carName.trim() !== '') {
          heroToSave.car = {
            name: this.carName.trim(),
            cool: this.carCool
          };
        } else {
          heroToSave.car = {
            name: "Без машины",
            cool: false
          };
        }

        this.$emit('save', heroToSave);
      }
    }
  };
</script>