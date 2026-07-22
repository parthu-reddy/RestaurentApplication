const oracledb = require('oracledb');
async function run() {
  try {
    const connection = await oracledb.getConnection({
      user: process.env.ORACLE_USER || 'food_delivery',
      password: process.env.ORACLE_PASSWORD || 'food_delivery123',
      connectString: process.env.ORACLE_URL || 'localhost:1521/XEPDB1'
    });
    const result = await connection.execute('SELECT order_id, status, additional_prep_time FROM restaurant_orders ORDER BY version DESC FETCH FIRST 5 ROWS ONLY');
    console.log(result.rows);
    await connection.close();
  } catch (err) {
    console.error(err);
  }
}
run();
