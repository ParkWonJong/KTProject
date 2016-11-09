<?php
  header("Content-Type: text/html; charset=UTF-8");
  $conn = mysqli_connect('127.0.0.1','root','1111');
  mysqli_select_db($conn,'crumb');
  $data_stream = "'".$_POST['id']."','".$_POST['name']."','".$_POST['phone']."','".$_POST['email']."','".$_POST['password']."'";
  $query = "insert into users(id,name,phone,email,password) values (".$data_stream.")";
  $result = mysqli_query($conn, $query);

  if($result)
    echo "1";
  else
    echo "-1";

    mysqli_close($conn);
  ?>
