package Hamming;

public interface Q<T> //interfejs za zajednički tip reda
{
    void put(T x) throws InterruptedException;
    T take() throws InterruptedException;
}
//interfejs : Svaka klasa koja implementira Q<T> mora imati dve metode:

//				put(T x) — stavlja element u red
//
//				take() — uzima element iz reda

//	🔹 4. Zašto je to korisno ###POLIMORFIZAM### // c++ virtual
//
//	Zamisli da nemaš interfejs — morao bi da koristiš tačno StdQ ili CustomBlockingQueue ime svuda u kodu.
//
//	Ali sada, pošto obe dele isti interfejs,
//	možeš da koristiš apstraktni tip Q<Long>:
//
//	Q<Long> q = mkQueue(useCustom, 0);
//
//
//	I ne moraš da znaš da li je to u stvari:
//
//	StdQ<Long> (ugrađena verzija) ili
//
//	CustomBlockingQueue<Long> (tvoja verzija)
//
//	Program radi identično.
//	To je polimorfizam — radiš s interfejsom, ne s konkretnom klasom.