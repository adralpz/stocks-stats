<script setup lang="ts">
const props = defineProps({ selectedStocks: Array, stocks: Array });

const selected = toRef(props, 'selectedStocks');
const stocks = toRef(props, 'stocks');
console.log(stocks.value);

const emit = defineEmits(['update:selectedStocks']);

watch(selected, (newValue) => {
  console.log("acaba de cambiar", newValue);
  emit('update:selectedStocks', newValue);
});
</script>

<template>
  <USelectMenu searchable searchable-placeholder="Search by name or color" v-model="selected" :options="stocks" multiple placeholder="Select showing stocks" />
</template>