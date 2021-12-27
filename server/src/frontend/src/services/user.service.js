import axios from 'axios';
import authHeader from './auth-header';

const API_URL = 'http://localhost:8082/api/';

class UserService {
    getPositions(startDate, endDate, terminalId) {
        return axios.get(API_URL + `positions?startDate=${startDate}&endDate=${endDate}&terminalId=${terminalId}`, { headers: authHeader() });
    }
}

export default new UserService();