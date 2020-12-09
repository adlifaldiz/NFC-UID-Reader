<?php
// extends class Model
class M_Uid extends CI_Model{
    // response jika field ada yang kosong
  public function empty_response(){
    $response['status']=false;
    $response['message']='Field tidak boleh kosong';
    return $response;
  }

  public function uid_exist($uid){
		$this->db->where('uid', $uid);
		$query = $this->db->get('tb_user');
		if( $query->num_rows() > 0 ){ 
			return TRUE; 
		} else { 
			return FALSE; 
		}
	}

  //function untuk tambah user
	public function tambah_user($uid)
	{
	
		if (empty($uid)) {
			$response['status'] = false;
			$response['message'] = 'Uid tidak boleh kosong.';
			return $response;
		} else {

			if (array_key_exists('uid',$_POST)) {
				if ( $this->uid_exist($this->input->post('uid')) == TRUE ) {
					$response['status'] = false;
					$response['message'] = 'Uid dengan nomor '.$uid.' sudah ada';
					return $response;
				} else {
					$data_user = array(
					"uid" => $uid
					);

					$insert_user = $this->db->insert("tb_user", $data_user);

					if ($insert_user) {
						$response['status'] = true;
						$response['message'] = 'Data ditambahkan.';
						return $response;
					} else {
						$response['status'] = false;
						$response['message'] = 'Data gagal ditambahkan.';
						return $response;
					}
				}
			}
		}
	}
}
