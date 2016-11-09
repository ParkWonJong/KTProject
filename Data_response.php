<?php
header("Content-Type: text/html; charset=UTF-8");
$conn = mysqli_connect('127.0.0.1','root','1111');
mysqli_select_db($conn,'crumb');
$result = mysqli_query($conn, 'SELECT * FROM coordinate');


if (! $result){
  throw new My_Db_Exception('Database error: '. mysql_error());
}
else {
  echo 1;
}


$resultArray = array();
while ( $row = mysqli_fetch_assoc ( $result )) {

  $arrayMiddle = array (
           "id" => ($row ['id']),
#          "id" => urlencode ($row ['id']), //if id has korean.
#          "latitude" => (double) $row ['latitude'],
#          "longtitude" => (double) $row ['longtitude']
          "coordinate" => array()
  );

  array_push($arrayMiddle['coordinate'], (double)$row['latitude']);
  array_push($arrayMiddle['coordinate'], (double)$row['longtitude']);

  array_push ( $resultArray, $arrayMiddle );
}
#echo $resultArray;

print_r (json_encode ( $resultArray));
# print_r ( urldecode ( json_encode ( $resultArray ) )); //decode array
mysqli_close ($conn);

?>
