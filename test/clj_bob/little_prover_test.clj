(ns clj-bob.little-prover-test
  (:require [clj-bob.little-prover :refer :all]
            [clj-bob.lang :as l]
            [clojure.test :refer :all]))

(declare dethm-align-align-ref)
(declare dethm-set?-atoms-ref)

(deftest acceptance
  (testing "evaluates to the same result as the reference implementation in scheme"
    (are [example scheme-output] (= scheme-output (example))
      chapter1-example1 '(car (cons (quote ham) (quote (eggs))))
      chapter1-example2 (quote 't)
      chapter1-example3 '(atom (cons (quote ham) (quote (eggs))))
      chapter1-example4 (quote 'nil)
      chapter1-example5 '(equal (quote flapjack) (atom (cons a b)))
      chapter1-example6 '(atom (cdr (cons (car (cons p q)) (quote ()))))
      chapter1-example7 '(atom (cdr (cons (car (cons p q)) (quote ()))))
      chapter1-example8 '(car (cons (equal (cons x y) (cons x y)) (quote (and crumpets))))
      chapter1-example9 '(equal (cons (quote bagels) (quote (and lox))) (cons x y))
      chapter1-example10 '(cons y (equal (car (cons (cdr x) (car y))) (equal (atom x) (quote nil))))
      chapter1-example11 '(cons y (equal (car (cons (cdr x) (car y))) (equal (atom x) (quote nil))))
      chapter1-example12 '(atom (car (cons (car a) (cdr b))))

      chapter2-example1 '(if (car (cons a b)) c c)
      chapter2-example2 '(if (atom (car a))
                           (if (equal (car a) (cdr a))
                             (quote hominy)
                             (quote grits))
                           (if (equal (cdr (car a)) (quote (hash browns)))
                             (cons (quote ketchup) (car a))
                             (cons (quote mustard) (car a))))
      chapter2-example3 '(cons (quote statement)
                               (cons (if (equal a (quote question))
                                       (cons n (quote (answer)))
                                       (cons n (quote (else))))
                                     (if (equal a (quote question))
                                       (cons n (quote (other answer)))
                                         (cons n (quote (other else))))))

      dethm-set?-atoms dethm-set?-atoms-ref

      dethm-align-align dethm-align-align-ref)))

(def dethm-set?-atoms-ref
  '((dethm atom/cons (x y) (equal (atom (cons x y)) (quote nil)))
    (dethm car/cons (x y) (equal (car (cons x y)) x))
    (dethm cdr/cons (x y) (equal (cdr (cons x y)) y))
    (dethm equal-same (x) (equal (equal x x) (quote t)))
    (dethm equal-swap (x y) (equal (equal x y) (equal y x)))
    (dethm if-same (x y) (equal (if x y y) y))
    (dethm if-true (x y) (equal (if (quote t) x y) x))
    (dethm if-false (x y) (equal (if (quote nil) x y) y))
    (dethm if-nest-e (x y z) (if x (quote t) (equal (if x y z) z)))
    (dethm if-nest-a (x y z) (if x (equal (if x y z) y) (quote t)))
    (dethm cons/car+cdr (x) (if (atom x) (quote t) (equal (cons (car x) (cdr x)) x)))
    (dethm equal-if (x y) (if (equal x y) (equal x y) (quote t)))
    (dethm natp/size (x) (equal (natp (size x)) (quote t)))
    (dethm size/car (x) (if (atom x) (quote t) (equal (< (size (car x)) (size x)) (quote t))))
    (dethm size/cdr (x) (if (atom x) (quote t) (equal (< (size (cdr x)) (size x)) (quote t))))
    (dethm associate-+ (a b c) (equal (+ (+ a b) c) (+ a (+ b c))))
    (dethm commute-+ (x y) (equal (+ x y) (+ y x)))
    (dethm natp/+ (x y) (if (natp x) (if (natp y) (equal (natp (+ x y)) (quote t)) (quote t)) (quote t)))
    (dethm positives-+ (x y) (if (< (quote 0) x) (if (< (quote 0) y) (equal (< (quote 0) (+ x y)) (quote t)) (quote t)) (quote t)))
    (dethm common-addends-< (x y z) (equal (< (+ x z) (+ y z)) (< x y)))
    (dethm identity-+ (x) (if (natp x) (equal (+ (quote 0) x) x) (quote t)))
    (defun list-induction (x) (if (atom x) (quote ()) (cons (car x) (list-induction (cdr x)))))
    (defun star-induction (x) (if (atom x) x (cons (star-induction (car x)) (star-induction (cdr x)))))
    (defun pair (x y) (cons x (cons y (quote ()))))
    (defun first-of (x) (car x))
    (defun second-of (x) (car (cdr x)))
    (defun in-pair? (xs) (if (equal (first-of xs) (quote ?)) (quote t) (equal (second-of xs) (quote ?))))
    (defun list0? (x) (equal x (quote ())))
    (defun list1? (x) (if (atom x) (quote nil) (list0? (cdr x))))
    (defun list2? (x) (if (atom x) (quote nil) (list1? (cdr x))))
    (defun list? (x) (if (atom x) (equal x (quote ())) (list? (cdr x))))
    (defun sub (x y) (if (atom y) (if (equal y (quote ?)) x y) (cons (sub x (car y)) (sub x (cdr y)))))
    (defun memb? (xs) (if (atom xs) (quote nil) (if (equal (car xs) (quote ?)) (quote t) (memb? (cdr xs)))))
    (defun remb (xs) (if (atom xs) (quote ()) (if (equal (car xs) (quote ?)) (remb (cdr xs)) (cons (car xs) (remb (cdr xs))))))
    (defun ctx? (x) (if (atom x) (equal x (quote ?)) (if (ctx? (car x)) (quote t) (ctx? (cdr x)))))
    (defun member? (x ys) (if (atom ys) (quote nil) (if (equal x (car ys)) (quote t) (member? x (cdr ys)))))
    (defun set? (xs) (if (atom xs) (quote t) (if (member? (car xs) (cdr xs)) (quote nil) (set? (cdr xs)))))
    (defun add-atoms (x ys) (if (atom x) (if (member? x ys) ys (cons x ys)) (add-atoms (car x) (add-atoms (cdr x) ys))))
    (defun atoms (x) (add-atoms x (quote ())))))

(def dethm-align-align-ref-pt1
  '((dethm atom-cons (x y) (equal (atom (cons x y)) (quote nil)))
    (dethm car-cons (x y) (equal (car (cons x y)) x))
    (dethm cdr-cons (x y) (equal (cdr (cons x y)) y))
    (dethm equal-same (x) (equal (equal x x) (quote t)))
    (dethm equal-swap (x y) (equal (equal x y) (equal y x)))
    (dethm if-same (x y) (equal (if x y y) y))
    (dethm if-true (x y) (equal (if (quote t) x y) x))
    (dethm if-false (x y) (equal (if (quote nil) x y) y))
    (dethm if-nest-e (x y z) (if x (quote t) (equal (if x y z) z)))
    (dethm if-nest-a (x y z) (if x (equal (if x y z) y) (quote t)))
    (dethm cons-car+cdr (x) (if (atom x) (quote t) (equal (cons (car x) (cdr x)) x)))
    (dethm equal-if (x y) (if (equal x y) (equal x y) (quote t)))
    (dethm natp-size (x) (equal (natp (size x)) (quote t)))
    (dethm size-car (x) (if (atom x) (quote t) (equal (< (size (car x)) (size x)) (quote t))))
    (dethm size-cdr (x) (if (atom x) (quote t) (equal (< (size (cdr x)) (size x)) (quote t))))
    (dethm associate-+ (a b c) (equal (+ (+ a b) c) (+ a (+ b c))))
    (dethm commute-+ (x y) (equal (+ x y) (+ y x)))
    (dethm natp-+ (x y) (if (natp x) (if (natp y) (equal (natp (+ x y)) (quote t)) (quote t)) (quote t)))
    (dethm positives-+ (x y) (if (< (quote 0) x) (if (< (quote 0) y) (equal (< (quote 0) (+ x y)) (quote t)) (quote t)) (quote t)))
    (dethm common-addends-< (x y z) (equal (< (+ x z) (+ y z)) (< x y)))
    (dethm identity-+ (x) (if (natp x) (equal (+ (quote 0) x) x) (quote t)))))

(def dethm-align-align-ref-pt2
  '((defun list-induction (x) (if (atom x) (quote ()) (cons (car x) (list-induction (cdr x)))))
    (defun star-induction (x) (if (atom x) x (cons (star-induction (car x)) (star-induction (cdr x)))))
    (defun pair (x y) (cons x (cons y (quote ()))))
    (defun first-of (x) (car x))
    (defun second-of (x) (car (cdr x)))
    (defun in-pair? (xs) (if (equal (first-of xs) (quote ?)) (quote t) (equal (second-of xs) (quote ?))))
    (defun list0? (x) (equal x (quote ())))
    (defun list1? (x) (if (atom x) (quote nil) (list0? (cdr x))))
    (defun list2? (x) (if (atom x) (quote nil) (list1? (cdr x))))
    (defun list? (x) (if (atom x) (equal x (quote ())) (list? (cdr x))))
    (defun sub (x y) (if (atom y) (if (equal y (quote ?)) x y) (cons (sub x (car y)) (sub x (cdr y)))))
    (defun memb? (xs) (if (atom xs) (quote nil) (if (equal (car xs) (quote ?)) (quote t) (memb? (cdr xs)))))
    (defun remb (xs) (if (atom xs) (quote ()) (if (equal (car xs) (quote ?)) (remb (cdr xs)) (cons (car xs) (remb (cdr xs))))))
    (defun ctx? (x) (if (atom x) (equal x (quote ?)) (if (ctx? (car x)) (quote t) (ctx? (cdr x)))))
    (defun member? (x ys) (if (atom ys) (quote nil) (if (equal x (car ys)) (quote t) (member? x (cdr ys)))))
    (defun set? (xs) (if (atom xs) (quote t) (if (member? (car xs) (cdr xs)) (quote nil) (set? (cdr xs)))))
    (defun add-atoms (x ys) (if (atom x) (if (member? x ys) ys (cons x ys)) (add-atoms (car x) (add-atoms (cdr x) ys))))
    (defun atoms (x) (add-atoms x (quote ())))
    (defun rotate (x) (cons (car (car x)) (cons (cdr (car x)) (cdr x))))
    (defun wt (x) (if (atom x) (quote 1) (+ (+ (wt (car x)) (wt (car x))) (wt (cdr x)))))))

(def dethm-align-align-ref
  "Unfortunately, the Clojure reader seems to have trouble parsing all of this at once."
  (concat dethm-align-align-ref-pt1
          dethm-align-align-ref-pt2))
