<?php

use Restserver\Libraries\REST_Controller;
require APPPATH . 'libraries/REST_Controller.php';

class Uid extends REST_Controller{

    // construct
  public function __construct(){
    parent::__construct();
    $this->load->model('M_Uid');
  }

  public function tambah_user_post()
  {
       $response = $this->M_Uid->tambah_user(
            $this->post("uid")
       );
       $this->response($response);
  }

}
?>