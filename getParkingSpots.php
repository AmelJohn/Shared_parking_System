<?php

include("connection.php");

$lat = $_POST['lat'];
$lon = $_POST['lon'];

$sql = "SELECT id, name, latitude, longitude, SQRT( POW(69.1 * (latitude - '$lat'), 2) + POW(69.1 * ('$lon' - longitude) * COS(latitude / 57.3), 2)) AS distance FROM tbl_parking_areas HAVING distance < 1 ORDER BY distance DESC";

	$result = mysqli_query($con,$sql);
	if(mysqli_num_rows($result) > 0)
	{
		while($row = mysqli_fetch_assoc($result))
			$data["data"][] = $row;
		echo$lat =  json_encode($data);	
	}
	else{
		echo "failed";
	}

?>