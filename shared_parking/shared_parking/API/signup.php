<?php

include("connection.php");

$fname = $_POST['fname']; 
$phone = $_POST['phone'];
$email = $_POST['email'];
$vno = $_POST['vno'];
$vtype = $_POST['vtype'];
$password = $_POST['password'];
 
 
$sql = " INSERT INTO tbl_user(name, phone, email, vehicle_no, vehicle_type, password) VALUES ('$fname','$phone','$email','$vno','$vtype','$password') ";

	if(mysqli_query($con,$sql))
		echo "success";
	else
		echo "Failed";
 
?>