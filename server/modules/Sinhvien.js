const mongoose = require("mongoose");
const nameDB = "thi_1";
mongoose
  .connect(
    `mongodb+srv://huy30062001pn:Huy2001abc@cluster0.gn6nytg.mongodb.net/${nameDB}?retryWrites=true&w=majority`
  )
  .then(() => console.log("✅ Connected database from mongodb."))
  .catch((error) =>
    console.error(`❌ Connect database is failed with error which is ${error}`)
  );
const schema = mongoose.Schema({
  Masv: {
    type: String,
    require: true,
  },
  hoten: {
    type: String,
    require: true,
  },
  email: {
    type: String,
    require: true,
  },
  diaChi: {
    type: String,
  },
  diem: {
    type: Number,
  },
  anh: {
    type: String,
  },
});

const Sinhvien = mongoose.model("Sinhvien", schema);
module.exports = Sinhvien;
