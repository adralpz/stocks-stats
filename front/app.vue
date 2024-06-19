<script lang="ts" setup>
import { Bar } from 'vue-chartjs'
const chartData = ref({
  labels: ['January', 'February', 'March', 'April', 'May'],
  datasets: [
    {
      label: 'Data One',
      backgroundColor: '#000000',
      data: [40, 20, 12, 50, 10],
    },
  ],
})
const chartOptions = ref({
  responsive: true,
  maintainAspectRatio: false,
})

const { data, error } = await useFetch('http://192.168.1.147:8080/api/stocks');
if (error) console.error(error.value);

const stocks = ref<string[]>(data.value as any); 

const page = ref(1);
const pageCount = ref(10);
const rows = computed(() => {
  const startIndex = (page.value - 1) * pageCount.value;
  const endIndex = startIndex + pageCount.value;
  return stocks.value.slice(startIndex, endIndex).map((stock, index) => ({
    id: index,
    name: stock,
  }));
});
</script>

<template>
  <UContainer>
    <UCard class="mt-10">
      <template #header>
        <div class="flex justify-between">
          <h1>Welcome to Nuxt UI Starter</h1>
          <ColorScheme>
            <USelect v-model="$colorMode.preference" :options="['system', 'light', 'dark']" />
          </ColorScheme>
        </div>
      </template>
      <div>
        <Bar :data="chartData" :options="chartOptions" />
      </div>

      <div>
        <UTable :rows="rows" />

        <div class="flex justify-end px-3 py-3.5 border-t border-gray-200 dark:border-gray-700">
          <UPagination v-model="page" :page-count="pageCount" :total="stocks.length" />
        </div>
      </div>
    </UCard>
  </UContainer>
</template>
