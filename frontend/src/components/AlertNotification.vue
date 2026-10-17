<template>
  <div>
    <div v-if="error" class="alert alert-danger alert-dismissible fade show" role="alert">
      <strong><i class="bi bi-exclamation-triangle-fill me-2"></i>Ошибка:</strong> {{ error.message }}
      <ul v-if="error.violations && error.violations.length" class="mb-0 mt-2">
        <li v-for="v in error.violations" :key="v.field">
          <strong>{{ v.field }}:</strong> {{ v.message }}
        </li>
      </ul>
      <button type="button" class="btn-close" @click="$emit('clear-error')"></button>
    </div>

    <div v-if="success" class="alert alert-success alert-dismissible fade show" role="alert">
      <i class="bi bi-check-circle-fill me-2"></i>{{ success }}
      <button type="button" class="btn-close" @click="$emit('clear-success')"></button>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    error: Object,
    success: String
  },
  emits: ['clear-error', 'clear-success']
};
</script>