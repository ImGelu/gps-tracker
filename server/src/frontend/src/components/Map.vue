<template>
  <div class="flex flex-row h-screen">
    <div class="flex flex-grow flex-col">
      <Navbar/>
      <div class="flex flex-col items-center justify-center p-6">
        <div class="shadow overflow-hidden sm:rounded-md">
          <div class="px-4 py-5 bg-white sm:p-6">
            <div class="mb-4 p-4 text-sm text-red-700 bg-red-100 rounded-lg dark:bg-red-200 dark:text-red-800"
                 role="alert" v-if="msg">
              {{ msg }}
            </div>
            <div class="col-span-6 sm:col-span-4">
              <label class="block text-sm font-medium text-gray-700 mb-2">Terminal ID</label>
              <div class="mb-3 pt-0">
                <input type="text" v-model="terminalId" placeholder="Enter the Terminal ID"
                       class="px-2 py-2 relative bg-white bg-white rounded text-sm border border-gray-300 outline-none focus:outline-none focus:ring w-full"/>
              </div>
            </div>

            <div class="grid grid-cols-6 gap-6">
              <div class="col-span-6 sm:col-span-3">
                <label class="block text-sm font-medium text-gray-700 mb-2">Start Date</label>
                <date-picker v-model="startDate" placeholder="Enter the Start Date" type="datetime" :show-second="false"
                             value-type="format" :default-value="new Date()"></date-picker>
              </div>

              <div class="col-span-6 sm:col-span-3">
                <label class="block text-sm font-medium text-gray-700 mb-2">End Date</label>
                <date-picker v-model="endDate" placeholder="Enter the End Date" type="datetime" :show-second="false"
                             value-type="format" :default-value="new Date()"></date-picker>
              </div>
            </div>
          </div>
          <div class="px-4 py-3 bg-gray-50 text-right sm:px-6">
            <button @click="search()"
                    class="inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-gray-600 hover:bg-gray-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-500">
              Search
            </button>
          </div>
        </div>
      </div>


      <div class="flex flex-grow lg:flex-row flex-col overflow-auto">
        <div class="flex flex-grow p-4">
          <GmapMap
              :center='center'
              :zoom='12'
              style='width:100%; height:100%;'
          >
            <GmapMarker
                :key="index"
                v-for="(m, index) in markers"
                :position="m.position"
                @click="center=m.position"
            />
          </GmapMap>
        </div>
        <div class="flex flex-grow-0 p-4" style="flex: 0 0 400px;">
          <div class="bg-white shadow sm:rounded-md p-4 overflow-auto w-full">
            <div class="divide-y-2 divide-gray-100" v-if="locations">
              <div class="p-3" v-for="location in locations" :key="location.id">
                <div class="float-right text-gray-500 text-xs mt-2">
                  {{ new Date(location.creationDate).toLocaleString("ro-RO") }}
                </div>
                <strong>{{ location.terminalId }}</strong><br/><small
                  class="text-xs text-gray-500">({{ new Number(location.latitude).toFixed(10) }},
                {{ new Number(location.longitude).toFixed(10) }})</small>
              </div>
            </div>
            <div class="text-center" v-if="!locations.length">No results</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Navbar from "@/components/Navbar";
import DatePicker from 'vue2-datepicker';
import 'vue2-datepicker/index.css';
import UserService from '@/services/user.service';

export default {
  name: 'Map',
  components: {
    Navbar, DatePicker
  },
  data() {
    return {
      startDate: "",
      endDate: "",
      terminalId: "",
      locations: [],

      center: {lat: 45.508, lng: -73.587},
      currentPlace: null,
      markers: [],
      places: [],
      msg: ""
    }
  },
  mounted() {
    this.geolocate();
  },
  methods: {
    async search() {
      var _this = this;
      if (this.startDate.length > 0 && this.endDate.length > 0 && this.terminalId.length > 0) {
        this.markers = [];
        await UserService.getPositions(this.startDate, this.endDate, this.terminalId).then(response => this.locations = response.data);
        this.locations.forEach(function (location) {
          _this.addMarker(parseFloat(location.latitude), parseFloat(location.longitude));
        });
        this.msg = "";
      } else {
        this.msg = "Please fill all fields!";
      }
    },
    addMarker(latitude, longitude) {
      const marker = {
        lat: latitude,
        lng: longitude,
      };
      this.markers.push({position: marker});
      this.center = marker;
      this.currentPlace = null;
    },
    geolocate: function () {
      navigator.geolocation.getCurrentPosition(position => {
        this.center = {
          lat: position.coords.latitude,
          lng: position.coords.longitude,
        };
      });
    },
    notAfterToday(date) {
      return date > new Date(new Date().setHours(0, 0, 0, 0));
    },
  }
}
</script>
<style>
.vue-map-container,
.vue-map-container .vue-map {
  width: 100%;
  height: 100%;
}

.vue-map {
  border-radius: 0.375rem;
  --tw-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06);
  box-shadow: var(--tw-ring-offset-shadow, 0 0 #0000), var(--tw-ring-shadow, 0 0 #0000), var(--tw-shadow);
}
</style>